package global;

public class functions {
	public int MAXPAGE = 10;

	public int countPages(int total, int n) {
		if (total % n == 0)
			return (int) (total / n);
		return (int) (total / n) + 1;
	}

	public String createPage(int total, String link, int nitem, int itemcurrent) {
		if (total < 1) {
			return null;
		}
		String ret = "";
		int pages = countPages(total, nitem);
		int step = 0;
		step = pages;
		if (itemcurrent > 0)
			ret += "<a title='Đầu tiên' href='" + link
					+ "0' class='lslink'><img src='images/icon-first.jpg' alt='Đầu tiên'></a> ";
		if (itemcurrent > 1)
			ret += "<a title='<< Về trước' href='" + link + (itemcurrent - 1)
					+ "' class='lslink'><img src='images/icon-previous.jpg' alt='<< Về trước'></a> ";
		int from = (itemcurrent - step > 0 ? itemcurrent - step : 0);
		int to = (itemcurrent + step < pages ? itemcurrent + step : pages);
		for (int i = from; i < to; i++) {
			if (i != itemcurrent)
				ret += " <a  class='lslink' href='" + link + i + "' >" + "<span>" + (i + 1) + "</span>" 
						+ "</a>";
			
			else
				ret += "<span>" + (i + 1) + "</span> ";
		}
		if ((itemcurrent < pages - 2) && (pages > 1))
			ret += "<a title='Tiếp theo >>' href='" + link + (itemcurrent + 1)
					+ "'><img src='images/icon-next.jpg' alt='Tiếp theo >>'></a> ";
		if (itemcurrent < pages - 1)
			ret += "<a title='Cuối cùng' href='" + link + (pages - 1)
					+ "'><img src='images/icon-last.jpg' alt='Cuối cùng'></a>";

		return ret;
	}
}
