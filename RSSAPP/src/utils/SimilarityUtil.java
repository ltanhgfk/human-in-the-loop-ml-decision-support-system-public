package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import libsvm.classify.StopWords;
import model.MMS;

/**
 * Tien ich tinh do tuong dong (similarity) giua 2 doan van ban (cau hoi) tieng Viet,
 * dung de tu dong tra loi mot cau hoi moi neu no giong (hoac gan giong) mot cau hoi
 * da duoc chuyen gia tra loi truoc do va da co san trong CSDL (tbl_mms, answered = true).
 *
 * Thuat toan su dung: Cosine Similarity tren vector tan so tu (term-frequency vector /
 * bag-of-words), sau khi van ban da duoc tach tu tieng Viet va loai bo stop-word bang
 * chinh lop libsvm.classify.StopWords co san cua he thong (de dam bao dong nhat cach
 * xu ly van ban voi module phan loai SVM dang dung trong MmsMain).
 *
 *      cosine(A,B) = (A . B) / (||A|| * ||B||)
 *
 * Ket qua tra ve nam trong doan [0,1]: cang gan 1 thi 2 cau hoi cang giong nhau,
 * bang 0 la hoan toan khong lien quan.
 */
public class SimilarityUtil {

	/**
	 * Ket qua tim kiem cau hoi giong nhat trong "kho cau hoi/tra loi" (knowledge base).
	 */
	public static class SimilarityResult {
		private final MMS match;
		private final double score;

		public SimilarityResult(MMS match, double score) {
			this.match = match;
			this.score = score;
		}

		/** Cau hoi (MMS) trong CSDL giong voi cau hoi moi nhat */
		public MMS getMatch() {
			return match;
		}

		/** Diem so tuong dong Cosine, trong khoang [0,1] */
		public double getScore() {
			return score;
		}
	}

	private SimilarityUtil() {
		//utility class, khong cho khoi tao
	}

	/**
	 * Tach tu, chuan hoa (lowercase, bo dau cau) va loai bo stop-word cho 1 cau van ban
	 * tieng Viet, tra ve vector tan so tu (term-frequency).
	 */
	private static Map<String, Integer> buildTermFrequencyVector(String text) {
		Map<String, Integer> vector = new HashMap<String, Integer>();
		if (text == null || text.trim().length() == 0) {
			return vector;
		}
		try {
			StopWords stopWords = new StopWords();
			//Tai su dung ham tach tu tieng Viet + loai bo stop-word co san cua he thong
			String cleaned = stopWords.removeStopWord(text);
			if (cleaned == null || cleaned.trim().length() == 0) {
				return vector;
			}
			String[] tokens = cleaned.trim().split("\\s+");
			for (String token : tokens) {
				if (token == null || token.length() == 0) {
					continue;
				}
				Integer count = vector.get(token);
				vector.put(token, count == null ? 1 : count + 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vector;
	}

	/**
	 * Tinh Cosine Similarity giua 2 cau van ban tieng Viet bat ky.
	 * @return gia tri trong khoang [0.0 , 1.0]
	 */
	public static double cosineSimilarity(String text1, String text2) {
		return cosineSimilarity(buildTermFrequencyVector(text1), buildTermFrequencyVector(text2));
	}

	private static double cosineSimilarity(Map<String, Integer> v1, Map<String, Integer> v2) {
		if (v1.isEmpty() || v2.isEmpty()) {
			return 0.0;
		}
		double dot = 0.0;
		for (Map.Entry<String, Integer> entry : v1.entrySet()) {
			Integer other = v2.get(entry.getKey());
			if (other != null) {
				dot += entry.getValue() * other;
			}
		}
		if (dot == 0.0) {
			return 0.0;
		}
		double norm1 = 0.0;
		for (int val : v1.values()) {
			norm1 += (double) val * val;
		}
		double norm2 = 0.0;
		for (int val : v2.values()) {
			norm2 += (double) val * val;
		}
		double denom = Math.sqrt(norm1) * Math.sqrt(norm2);
		if (denom == 0.0) {
			return 0.0;
		}
		return dot / denom;
	}

	/**
	 * Tim trong danh sach cac cau hoi da duoc tra loi truoc do (knowledgeBase) cau hoi
	 * co do tuong dong Cosine cao nhat so voi cau hoi moi (newQuestion).
	 *
	 * @param newQuestion   noi dung tin nhan/cau hoi moi nhan duoc tu nha nong
	 * @param knowledgeBase danh sach MMS da co answered = true (dung lam CSDL cau hoi/tra loi mau)
	 * @return SimilarityResult chua cau hoi giong nhat va diem so tuong dong,
	 *         hoac null neu khong tim duoc ket qua nao phu hop (knowledgeBase rong,
	 *         cau hoi moi khong co tu nao con lai sau khi loai stop-word, ...)
	 */
	public static SimilarityResult findMostSimilar(String newQuestion, ArrayList<MMS> knowledgeBase) {
		if (newQuestion == null || newQuestion.trim().length() == 0
				|| knowledgeBase == null || knowledgeBase.isEmpty()) {
			return null;
		}
		Map<String, Integer> newVector = buildTermFrequencyVector(newQuestion);
		if (newVector.isEmpty()) {
			return null;
		}
		MMS bestMatch = null;
		double bestScore = -1.0;
		for (MMS candidate : knowledgeBase) {
			if (candidate == null
					|| candidate.getmsg() == null || candidate.getmsg().trim().length() == 0
					|| candidate.getreplymsg() == null || candidate.getreplymsg().trim().length() == 0) {
				//Bo qua cac ban ghi khong co cau hoi hoac chua co noi dung tra loi de dung lam mau
				continue;
			}
			Map<String, Integer> candidateVector = buildTermFrequencyVector(candidate.getmsg());
			double score = cosineSimilarity(newVector, candidateVector);
			if (score > bestScore) {
				bestScore = score;
				bestMatch = candidate;
			}
		}
		if (bestMatch == null) {
			return null;
		}
		return new SimilarityResult(bestMatch, bestScore);
	}
}
