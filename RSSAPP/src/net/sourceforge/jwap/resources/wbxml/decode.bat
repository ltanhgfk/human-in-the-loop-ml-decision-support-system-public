CLS


REM **Testing for WBXML to WML conversion**

REM rmdir .\testsuite\wml\output /q /s
REM mkdir .\testsuite\wml\output

java -cp ./bin;./lib/xercesImpl.jar;./lib/xmlParserAPIs.jar WBXMLParser -decode ./testsuite/wml/input/jamba.wbxml ./testsuite/wml/output/jamba.wml
java -cp ./bin;./lib/xercesImpl.jar;./lib/xmlParserAPIs.jar WBXMLParser -decode ./testsuite/wml/input/nokiawapcontent.wbxml ./testsuite/wml/output/nokiawapcontent.wml
java -cp ./bin;./lib/xercesImpl.jar;./lib/xmlParserAPIs.jar WBXMLParser -decode ./testsuite/wml/input/index.wbxml ./testsuite/wml/output/index.wml


REM **Testing for WBXML to SyncML conversion**


rmdir .\testsuite\syncml\output /q /s
mkdir .\testsuite\syncml\output 


java -cp ./bin;./lib/xercesImpl.jar;./lib/xmlParserAPIs.jar WBXMLParser -decode ./testsuite/syncml/input/syncml-001.wbxml ./testsuite/syncml/output/syncml-001.xml
java -cp ./bin;./lib/xercesImpl.jar;./lib/xmlParserAPIs.jar WBXMLParser -decode ./testsuite/syncml/input/syncml-002.wbxml ./testsuite/syncml/output/syncml-002.xml
java -cp ./bin;./lib/xercesImpl.jar;./lib/xmlParserAPIs.jar WBXMLParser -decode ./testsuite/syncml/input/syncml-003.wbxml ./testsuite/syncml/output/syncml-003.xml
java -cp ./bin;./lib/xercesImpl.jar;./lib/xmlParserAPIs.jar WBXMLParser -decode ./testsuite/syncml/input/syncml-004.wbxml ./testsuite/syncml/output/syncml-004.xml
java -cp ./bin;./lib/xercesImpl.jar;./lib/xmlParserAPIs.jar WBXMLParser -decode ./testsuite/syncml/input/syncml-005.wbxml ./testsuite/syncml/output/syncml-005.xml
java -cp ./bin;./lib/xercesImpl.jar;./lib/xmlParserAPIs.jar WBXMLParser -decode ./testsuite/syncml/input/syncml-006.wbxml ./testsuite/syncml/output/syncml-006.xml
java -cp ./bin;./lib/xercesImpl.jar;./lib/xmlParserAPIs.jar WBXMLParser -decode ./testsuite/syncml/input/syncml-007.wbxml ./testsuite/syncml/output/syncml-007.xml
java -cp ./bin;./lib/xercesImpl.jar;./lib/xmlParserAPIs.jar WBXMLParser -decode ./testsuite/syncml/input/syncml-008.wbxml ./testsuite/syncml/output/syncml-008.xml
java -cp ./bin;./lib/xercesImpl.jar;./lib/xmlParserAPIs.jar WBXMLParser -decode ./testsuite/syncml/input/syncml-009.wbxml ./testsuite/syncml/output/syncml-009.xml
java -cp ./bin;./lib/xercesImpl.jar;./lib/xmlParserAPIs.jar WBXMLParser -decode ./testsuite/syncml/input/syncml-010.wbxml ./testsuite/syncml/output/syncml-010.xml
java -cp ./bin;./lib/xercesImpl.jar;./lib/xmlParserAPIs.jar WBXMLParser -decode ./testsuite/syncml/input/syncml-011.wbxml ./testsuite/syncml/output/syncml-011.xml
java -cp ./bin;./lib/xercesImpl.jar;./lib/xmlParserAPIs.jar WBXMLParser -decode ./testsuite/syncml/input/syncml-012.wbxml ./testsuite/syncml/output/syncml-012.xml

