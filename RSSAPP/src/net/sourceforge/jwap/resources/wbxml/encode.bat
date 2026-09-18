CLS
REM **Testing for WML to WBXML conversion**

REM rmdir .\testsuite\wml\output /q /s
REM mkdir .\testsuite\wml\output

REM set classpath=.;..;../../../../../../../../../target/classes;../../../../../../../lib/xml-apis-1.0.b2.jar;../../../../../../../lib/xerces-2.4.0.jar

java -cp ../../../../../ net.sourceforge.jwap.util.wbxml.WBXMLConverter -encode testsuite/wml/input/jamba.wml testsuite/wml/output/jamba.wbxml
java -cp ../../../../../ net.sourceforge.jwap.util.wbxml.WBXMLConverter -encode testsuite/wml/input/nokiawapcontent.wml testsuite/wml/output/nokiawapcontent.wbxml
java -cp ../../../../../ net.sourceforge.jwap.util.wbxml.WBXMLConverter -encode testsuite/wml/input/index.wml testsuite/wml/output/index.wbxml



REM **Testing for SyncML to WBXML conversion**
REM rmdir .\testsuite\syncml\output /q /s
REM mkdir .\testsuite\syncml\output

java -cp ../../../../../ net.sourceforge.jwap.util.wbxml.WBXMLConverter -encode ./testsuite/syncml/input/syncml-001.xml ./testsuite/syncml/output/syncml-001.wbxml
java -cp ../../../../../ net.sourceforge.jwap.util.wbxml.WBXMLConverter -encode ./testsuite/syncml/input/syncml-002.xml ./testsuite/syncml/output/syncml-002.wbxml
java -cp ../../../../../ net.sourceforge.jwap.util.wbxml.WBXMLConverter -encode ./testsuite/syncml/input/syncml-003.xml ./testsuite/syncml/output/syncml-003.wbxml
java -cp ../../../../../ net.sourceforge.jwap.util.wbxml.WBXMLConverter -encode ./testsuite/syncml/input/syncml-004.xml ./testsuite/syncml/output/syncml-004.wbxml
java -cp ../../../../../ net.sourceforge.jwap.util.wbxml.WBXMLConverter -encode ./testsuite/syncml/input/syncml-005.xml ./testsuite/syncml/output/syncml-005.wbxml
java -cp ../../../../../ net.sourceforge.jwap.util.wbxml.WBXMLConverter -encode ./testsuite/syncml/input/syncml-006.xml ./testsuite/syncml/output/syncml-006.wbxml
java -cp ../../../../../ net.sourceforge.jwap.util.wbxml.WBXMLConverter -encode ./testsuite/syncml/input/syncml-007.xml ./testsuite/syncml/output/syncml-007.wbxml
java -cp ../../../../../ net.sourceforge.jwap.util.wbxml.WBXMLConverter -encode ./testsuite/syncml/input/syncml-008.xml ./testsuite/syncml/output/syncml-008.wbxml
java -cp ../../../../../ net.sourceforge.jwap.util.wbxml.WBXMLConverter -encode ./testsuite/syncml/input/syncml-009.xml ./testsuite/syncml/output/syncml-009.wbxml
java -cp ../../../../../ net.sourceforge.jwap.util.wbxml.WBXMLConverter -encode ./testsuite/syncml/input/syncml-010.xml ./testsuite/syncml/output/syncml-010.wbxml
java -cp ../../../../../ net.sourceforge.jwap.util.wbxml.WBXMLConverter -encode ./testsuite/syncml/input/syncml-011.xml ./testsuite/syncml/output/syncml-011.wbxml
java -cp ../../../../../ net.sourceforge.jwap.util.wbxml.WBXMLConverter -encode ./testsuite/syncml/input/syncml-012.xml ./testsuite/syncml/output/syncml-012.wbxml

