package com.pkm.medicalinventory.externalapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TransactionRawData {

	static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");
	static Map<String, Date> adjustedDates = null;
	//kodetransaksi;kodejenistransaksi;kodepegawai;kodepemasok;kodepasien;kodepuskesmas;tanggal;wilayahtransaksi
	static final String MAPPED_DATES = "09liskCMwk7Ig;28/10/2018\r\n" + 
			"0EX6s80ywK8Fg;01/09/2018\r\n" + 
			"0RR0sXiiwC3Yg;03/09/2018\r\n" + 
			"1a7EsAz3wKpKg;04/10/2018\r\n" + 
			"1F2osA9Zwrs4g;22/11/2018\r\n" + 
			"1hXBsCLgwQcTg;08/11/2018\r\n" + 
			"1JqzsA0lwLqug;30/09/2018\r\n" + 
			"1O1hs4jNwjiRg;19/11/2018\r\n" + 
			"1V6gs9c5w8sMg;04/10/2018\r\n" + 
			"2AL6sMkiw30ng;03/09/2018\r\n" + 
			"2BB6sEcSwVFbg;30/09/2018\r\n" + 
			"3896s6r0wr8rg;17/09/2018\r\n" + 
			"3bNTs8ZHwWH0g;12/11/2018\r\n" + 
			"3Jcts9Lcwg22g;21/11/2018\r\n" + 
			"3lPUs13jwIkqg;26/09/2018\r\n" + 
			"3QZ6sad8wfnsg;08/11/2018\r\n" + 
			"3RL9sa08wq38g;03/09/2018\r\n" + 
			"3w5Ss3frwS2lg;18/03/2019\r\n" + 
			"3Zf8s7EAwCglg;21/11/2018\r\n" + 
			"4m8Js534wKdqg;31/01/2018\r\n" + 
			"4OM1se71wMLPg;31/01/2018\r\n" + 
			"4s6Ms5NGwQfwg;31/01/2018\r\n" + 
			"4ve1sRC8w5LWg;12/01/2019\r\n" + 
			"535jsHx7w67Zg;21/11/2018\r\n" + 
			"53iEsnRhw4vCg;04/09/2018\r\n" + 
			"53RNsVqpwkeng;28/10/2018\r\n" + 
			"5AWpsufrwzv8g;16/03/2019\r\n" + 
			"5e1MsxhlwoRZg;01/09/2018\r\n" + 
			"5hW2s35xwvKPg;03/09/2018\r\n" + 
			"5lGgswoswEyZg;04/11/2018\r\n" + 
			"5Pads7nwwR2mg;14/11/2018\r\n" + 
			"5SJ1sYmfwd0Mg;13/11/2018\r\n" + 
			"684lsUG6wzz1g;02/11/2018\r\n" + 
			"6J8WsZ6awpF5g;15/11/2018\r\n" + 
			"6UvTsHo6wc7Ig;20/11/2018\r\n" + 
			"7bcosKhewkA5g;28/10/2018\r\n" + 
			"7BS7sF9Vw0J8g;12/11/2018\r\n" + 
			"7F7ms7a3wpi9g;12/11/2018\r\n" + 
			"7gFKsuh3wkQhg;12/01/2019\r\n" + 
			"7Q4BszDswR72g;10/11/2018\r\n" + 
			"7Vq8s1S4wqLMg;20/09/2018\r\n" + 
			"894fsaLlw8Z1g;05/09/2018\r\n" + 
			"8g9lsux9w5h6g;30/09/2018\r\n" + 
			"8J2issP4w8dmg;17/09/2018\r\n" + 
			"8LYEsXNfwHcwg;04/11/2018\r\n" + 
			"8Vxns791w6D7g;31/10/2018\r\n" + 
			"91xhsm8Qw5XZg;21/11/2018\r\n" + 
			"92rmsw4uw9Wvg;25/03/2019\r\n" + 
			"98vZsp10wOsag;07/11/2018\r\n" + 
			"9E2RsS9ewDoxg;26/09/2018\r\n" + 
			"9h98sj95whGQg;12/11/2018\r\n" + 
			"9KO9sI92wIcqg;20/11/2018\r\n" + 
			"9l57sBi5wG8Ig;12/11/2018\r\n" + 
			"9VJ6soVMwg7vg;02/11/2018\r\n" + 
			"a31fs1E3wIjag;01/10/2018\r\n" + 
			"A4k5snMSw3Uqg;06/03/2019\r\n" + 
			"A8uGscaBw8KBg;02/11/2018\r\n" + 
			"AeSys7l4w0lig;14/09/2018\r\n" + 
			"AG8bsCj0wgzOg;21/11/2018\r\n" + 
			"aP71s6HUwU7dg;03/09/2018\r\n" + 
			"auto1791;01/06/2017\r\n" + 
			"auto1792;01/06/2017\r\n" + 
			"auto201601;01/01/2016\r\n" + 
			"auto201601k;01/01/2016\r\n" + 
			"auto201603;01/02/2016\r\n" + 
			"auto201603k;01/02/2016\r\n" + 
			"auto201605;01/03/2016\r\n" + 
			"auto201605k;01/03/2016\r\n" + 
			"auto201607;01/04/2016\r\n" + 
			"auto201607k;01/04/2016\r\n" + 
			"auto201609;01/05/2016\r\n" + 
			"auto201609k;01/05/2016\r\n" + 
			"auto201611;01/06/2016\r\n" + 
			"auto201611k;01/06/2016\r\n" + 
			"auto201613;01/07/2016\r\n" + 
			"auto201613k;01/07/2016\r\n" + 
			"auto201615;01/08/2016\r\n" + 
			"auto201615k;01/08/2016\r\n" + 
			"auto201617;01/09/2016\r\n" + 
			"auto201617k;01/09/2016\r\n" + 
			"auto201619;01/10/2016\r\n" + 
			"auto201619k;01/10/2016\r\n" + 
			"auto201621;01/11/2016\r\n" + 
			"auto201621k;01/11/2016\r\n" + 
			"auto201623;01/12/2016\r\n" + 
			"auto201623k;01/12/2016\r\n" + 
			"auto2019;01/08/2017\r\n" + 
			"auto2020;01/08/2017\r\n" + 
			"auto2117;01/07/2017\r\n" + 
			"auto2118;01/07/2017\r\n" + 
			"auto3218;01/02/2017\r\n" + 
			"auto3219;01/02/2017\r\n" + 
			"auto3313;01/04/2017\r\n" + 
			"auto3314;01/04/2017\r\n" + 
			"auto3613;01/11/2017\r\n" + 
			"auto3614;01/11/2017\r\n" + 
			"auto4254;01/01/2017\r\n" + 
			"auto4255;01/01/2017\r\n" + 
			"auto6301;01/12/2017\r\n" + 
			"auto6302;01/12/2017\r\n" + 
			"auto6634;01/10/2017\r\n" + 
			"auto6635;01/10/2017\r\n" + 
			"auto6936;01/09/2017\r\n" + 
			"auto6937;01/09/2017\r\n" + 
			"auto7368;01/03/2017\r\n" + 
			"auto7369;01/03/2017\r\n" + 
			"auto8325;01/05/2017\r\n" + 
			"auto8326;01/05/2017\r\n" + 
			"AZaus31KwK08g;12/11/2018\r\n" + 
			"B12dsZ4yw7J7g;04/11/2018\r\n" + 
			"BcK2s0j1wCG2g;30/12/2018\r\n" + 
			"bEH2slvIwuDqg;02/11/2018\r\n" + 
			"cH1Psn96wm45g;12/11/2018\r\n" + 
			"cmuasy5JwmUXg;21/11/2018\r\n" + 
			"DBxFs0PRwPJug;05/09/2018\r\n" + 
			"DCVZsAH1wp89g;10/11/2018\r\n" + 
			"dTY5sVfMwe5dg;03/09/2013\r\n" + 
			"e23jsm31wUNrg;01/09/2018\r\n" + 
			"E429skxTw69zg;12/11/2018\r\n" + 
			"eC3GshCpwFl6g;01/09/2018\r\n" + 
			"El6KsMntwtGBg;12/11/2018\r\n" + 
			"eLyes9B9wUFbg;03/09/2018\r\n" + 
			"EWQ7sHviwMq1g;10/11/2018\r\n" + 
			"F1Nss0rnw0bWg;02/11/2018\r\n" + 
			"F6IOsq6EwFl8g;03/09/2018\r\n" + 
			"f8yWs7j9w4jQg;10/11/2018\r\n" + 
			"fBo3s862w5Fxg;19/09/2018\r\n" + 
			"FkK2s1BHwy8ag;01/09/2018\r\n" + 
			"G0K4s20jw5zwg;06/11/2018\r\n" + 
			"Ge0lsi6qwvp5g;30/12/2018\r\n" + 
			"gG7RsVkyw4M5g;28/10/2018\r\n" + 
			"GHUOspb3w4vTg;02/11/2018\r\n" + 
			"H3j6sjrBwuGfg;07/11/2018\r\n" + 
			"hlxVstzqwxX2g;31/01/2018\r\n" + 
			"HT9MsI2cwz4kg;22/11/2018\r\n" + 
			"HuDOs2Xdw767g;12/11/2018\r\n" + 
			"I48CsSwFwN4ng;14/03/2019\r\n" + 
			"I5iOs2YPwG19g;06/03/2019\r\n" + 
			"I8Frse0DwOo4g;30/09/2018\r\n" + 
			"ia82sc6YwC4Pg;01/01/2018\r\n" + 
			"iOYjskgawAvAg;03/12/2018\r\n" + 
			"j603s88TwImzg;02/11/2018\r\n" + 
			"Jiwus1J5wbE3g;01/09/2018\r\n" + 
			"k0fgskY8wpQ8g;03/09/2018\r\n" + 
			"K53us07UwFFWg;02/09/2018\r\n" + 
			"k6c0s4qTwbe3g;06/11/2018\r\n" + 
			"k9vnsFoxwinig;02/11/2018\r\n" + 
			"kHorsDV0wxUOg;14/11/2018\r\n" + 
			"kRPas2pNwO6yg;29/09/2018\r\n" + 
			"ky64s824w7NWg;29/01/2018\r\n" + 
			"KZRcsh1zwIIgg;21/11/2018\r\n" + 
			"L102sl9Hw898g;01/09/2018\r\n" + 
			"l2d6skm2wEMzg;31/01/2018\r\n" + 
			"La33siedws54g;28/10/2018\r\n" + 
			"LF5Oss8ow2q4g;04/10/2018\r\n" + 
			"lFBFs10dwE9Sg;21/11/2018\r\n" + 
			"lH7ts1UawuoFg;26/09/2018\r\n" + 
			"LoiFsO61wZ71g;12/01/2019\r\n" + 
			"LoN0s6RNwz22g;25/09/2018\r\n" + 
			"m35Cs437weGWg;10/11/2018\r\n" + 
			"MLFdsxFjwX77g;03/09/2018\r\n" + 
			"mm86sDk7wrj8g;31/01/2018\r\n" + 
			"mnv8sa1lw1M4g;07/10/2018\r\n" + 
			"muuFs7w2w56yg;06/03/2019\r\n" + 
			"mxz8sz79wVnPg;12/11/2018\r\n" + 
			"MZFOsB09w0T1g;10/11/2018\r\n" + 
			"N04usU1Lwh8fg;31/10/2018\r\n" + 
			"nd2as06WwvH4g;21/11/2018\r\n" + 
			"np7AsphhwF9yg;26/09/2018\r\n" + 
			"NqG3s3n5wsTyg;12/01/2019\r\n" + 
			"nQr0sTdFwk6Qg;08/11/2018\r\n" + 
			"nw7lsb6kwBQeg;30/09/2018\r\n" + 
			"O57zsaqmwQd6g;21/11/2018\r\n" + 
			"oCIzssv8wVohg;30/09/2018\r\n" + 
			"oHrZsoTXwR76g;02/09/2018\r\n" + 
			"OJ2dsixBwIYXg;01/10/2018\r\n" + 
			"Oq29sSV9wd5gg;30/09/2018\r\n" + 
			"oR22sP48wXhYg;03/09/2018\r\n" + 
			"p28OsoTaw2DNg;31/10/2018\r\n" + 
			"p2wQs228wOt9g;12/11/2018\r\n" + 
			"p67isZvZw4PPg;30/09/2018\r\n" + 
			"pL6rsltWwQstg;08/11/2018\r\n" + 
			"PTG7s9BawFvUg;01/11/2018\r\n" + 
			"Py9rsrs7wYU8g;04/10/2018\r\n" + 
			"q66vsi3rwWIVg;04/09/2018\r\n" + 
			"qJnJsmMawa7gg;10/11/2018\r\n" + 
			"qn9fsQ1fwqgUg;17/09/2018\r\n" + 
			"Qso1stlgwZt4g;17/03/2019\r\n" + 
			"RibasmR2wF55g;30/09/2018\r\n" + 
			"Rn0JsjYtwZ2ug;04/11/2018\r\n" + 
			"S3bR95U61W0jENWN4GGwk;10/04/2019\r\n" + 
			"Sb6EUUH35AkPT30;12/08/2019\r\n" + 
			"Sf8R34UF1W1qE5aNg0G7Q;01/04/2019\r\n" + 
			"SgwR6NU2JWjGEHPN0WGCE;09/04/2019\r\n" + 
			"sJxFsO6VwOk0g;12/11/2018\r\n" + 
			"Sq3ROfUEgW2eEj5NY0Glf;10/04/2019\r\n" + 
			"St6Pssduw5o6g;03/09/2018\r\n" + 
			"su8bsI9vwRaOg;31/10/2018\r\n" + 
			"SViR9wU9wWdbEB3N2MGLS;09/04/2019\r\n" + 
			"t303srBzwIlqg;12/11/2018\r\n" + 
			"T96MsT1awIw2g;21/11/2018\r\n" + 
			"tm63syfJwEGTg;14/11/2018\r\n" + 
			"tMCXs8ICw7b1g;09/10/2018\r\n" + 
			"U9N7sixLwVmyg;02/11/2018\r\n" + 
			"UfqZsh0EwYEig;08/11/2018\r\n" + 
			"Umd7sW50w0B1g;30/09/2018\r\n" + 
			"vHxvsaX4w7Bjg;30/09/2018\r\n" + 
			"w0bFsYe1w0T7g;13/12/2018\r\n" + 
			"W2uesoHMw60mg;05/09/2018\r\n" + 
			"wx50sUd2wbOig;12/11/2018\r\n" + 
			"X1BpsPqEw8ggg;03/11/2018\r\n" + 
			"X54zsQoPwGhIg;03/11/2018\r\n" + 
			"XBK1sRE5w938g;17/01/2018\r\n" + 
			"xPocsZXgw23Kg;22/11/2018\r\n" + 
			"XPwFs121ww2ag;10/11/2018\r\n" + 
			"Xy41sVRdws62g;15/11/2018\r\n" + 
			"y19Es38ewSl5g;30/09/2018\r\n" + 
			"Y63Rsbd3whw2g;31/10/2018\r\n" + 
			"ypFMsIm7w307g;03/09/2018\r\n" + 
			"z4Q1s2PPwDdWg;17/03/2019\r\n" + 
			"zr79sH7Mwdpug;20/11/2018\r\n" + 
			"zuqtsIE2wEF1g;06/11/2018";
	static final String DATA = "09liskCMwk7Ig;2;2;;I0cKu3s6t0m7r;;43401;1\r\n" + 
			"0EX6s80ywK8Fg;2;2;;m6cOumsotGm8r;;43344;1\r\n" + 
			"0RR0sXiiwC3Yg;2;3;;1mc5u9s1t0mtr;;43346;3\r\n" + 
			"1a7EsAz3wKpKg;2;2;;3JcKuns0t6mur;;43377;1\r\n" + 
			"1F2osA9Zwrs4g;2;2;;7ycCuMs3t3mxr;;43426;1\r\n" + 
			"1hXBsCLgwQcTg;2;2;;I0cKu3s6t0m7r;;43412;1\r\n" + 
			"1JqzsA0lwLqug;2;2;;j2cnu2sjt1m5r;;43373;21\r\n" + 
			"1O1hs4jNwjiRg;2;2;;36cEuUsptGm5r;;43423;1\r\n" + 
			"1V6gs9c5w8sMg;1;2;SUPPLIER5;;1;43377;1\r\n" + 
			"2AL6sMkiw30ng;2;3;;;4;43346;1\r\n" + 
			"2BB6sEcSwVFbg;3;2;SUPPLIER6;;;43373;1\r\n" + 
			"3896s6r0wr8rg;1;2;SUPPLIER5;;1;43360;1\r\n" + 
			"3bNTs8ZHwWH0g;2;2;;kDcwubsWtjmyr;;43416;1\r\n" + 
			"3Jcts9Lcwg22g;1;2;SUPPLIER3;;1;43425;1\r\n" + 
			"3lPUs13jwIkqg;1;2;SUPPLIER6;;1;43369;1\r\n" + 
			"3QZ6sad8wfnsg;1;2;SUPPLIER4;;1;43412;1\r\n" + 
			"3RL9sa08wq38g;1;2;SUPPLIER7;;1;43346;1\r\n" + 
			"3w5Ss3frwS2lg;2;2;;8ockuBsat8m0r;;43542;1\r\n" + 
			"3Zf8s7EAwCglg;1;2;SUPPLIER2;;1;43425;1\r\n" + 
			"4m8Js534wKdqg;1;2;SUPPLIER5;;1;43131;1\r\n" + 
			"4OM1se71wMLPg;1;2;SUPPLIER3;;1;43131;1\r\n" + 
			"4s6Ms5NGwQfwg;2;2;;1lc0ujsxt4m5r;;43131;1\r\n" + 
			"4ve1sRC8w5LWg;2;2;;kDcwubsWtjmyr;;43477;2\r\n" + 
			"535jsHx7w67Zg;2;2;;;17;43425;1\r\n" + 
			"53iEsnRhw4vCg;2;3;;customer00;;43347;1\r\n" + 
			"53RNsVqpwkeng;1;2;SUPPLIER7;;1;43401;1\r\n" + 
			"5AWpsufrwzv8g;1;2;SUPPLIER6;;1;43540;1\r\n" + 
			"5e1MsxhlwoRZg;1;2;SUPPLIER7;;1;43344;1\r\n" + 
			"5hW2s35xwvKPg;2;3;;f2cquZs8t3mUr;;43346;3\r\n" + 
			"5lGgswoswEyZg;1;2;SUPPLIER5;;1;43408;1\r\n" + 
			"5Pads7nwwR2mg;1;2;SUPPLIER2;;1;43418;1\r\n" + 
			"5SJ1sYmfwd0Mg;2;2;;i7cLuEsot2m4r;;43417;1\r\n" + 
			"684lsUG6wzz1g;2;2;;0kceu9sUtEm8r;;43406;4\r\n" + 
			"6J8WsZ6awpF5g;2;2;;zhcJuYsptLm5r;;43419;1\r\n" + 
			"6UvTsHo6wc7Ig;2;2;;cGc5uEswtcm7r;;43424;1\r\n" + 
			"7bcosKhewkA5g;2;2;;29cHuQsStqm5r;;43401;1\r\n" + 
			"7BS7sF9Vw0J8g;2;2;;iOc1u9s5t7mfr;;43416;1\r\n" + 
			"7F7ms7a3wpi9g;2;2;;;5;43416;1\r\n" + 
			"7gFKsuh3wkQhg;2;2;;;2;43477;1\r\n" + 
			"7Q4BszDswR72g;2;2;;3tcbuKsxtymgr;;43414;1\r\n" + 
			"7Vq8s1S4wqLMg;2;3;;tVc5uFsKt8mtr;;43363;1\r\n" + 
			"894fsaLlw8Z1g;1;2;SUPPLIER4;;1;43348;1\r\n" + 
			"8g9lsux9w5h6g;2;2;;09cxuqsFt6mXr;;43373;1\r\n" + 
			"8J2issP4w8dmg;1;2;SUPPLIER7;;1;43360;1\r\n" + 
			"8LYEsXNfwHcwg;2;2;;kDcwubsWtjmyr;;43408;1\r\n" + 
			"8Vxns791w6D7g;2;3;;A7c2u5s4tYm6r;;43404;1\r\n" + 
			"91xhsm8Qw5XZg;2;2;;4wcZu7s1tOmor;;43425;1\r\n" + 
			"92rmsw4uw9Wvg;1;2;SUPPLIER11;;1;43549;1\r\n" + 
			"98vZsp10wOsag;1;2;SUPPLIER2;;1;43411;1\r\n" + 
			"9E2RsS9ewDoxg;1;2;SUPPLIER3;;1;43369;1\r\n" + 
			"9h98sj95whGQg;2;2;;;2;43416;1\r\n" + 
			"9KO9sI92wIcqg;2;2;;f8cCuostt5mxr;;43424;1\r\n" + 
			"9l57sBi5wG8Ig;2;2;;V7c7uzs4tXmxr;;43416;15\r\n" + 
			"9VJ6soVMwg7vg;2;2;;o8cwuasmtYm3r;;43406;1\r\n" + 
			"a31fs1E3wIjag;2;2;;j2cnu2sjt1m5r;;43374;1\r\n" + 
			"A4k5snMSw3Uqg;1;2;SUPPLIER5;;1;43530;1\r\n" + 
			"A8uGscaBw8KBg;2;2;;3tcbuKsxtymgr;;43406;1\r\n" + 
			"AeSys7l4w0lig;2;2;;m1ciuHs7tLmfr;;43357;1\r\n" + 
			"AG8bsCj0wgzOg;1;2;SUPPLIER7;;1;43425;1\r\n" + 
			"aP71s6HUwU7dg;1;2;SUPPLIER4;;1;43346;1\r\n" + 
			"auto1791;1;3;SUPPLIER00;;1;42887;1\r\n" + 
			"auto1792;2;3;;customer00;;42887;1\r\n" + 
			"auto201601;2;3;;customer00;;42370;1\r\n" + 
			"auto201601k;1;3;SUPPLIER00;;1;42370;1\r\n" + 
			"auto201603;2;3;;customer00;;42401;1\r\n" + 
			"auto201603k;1;3;SUPPLIER00;;1;42401;1\r\n" + 
			"auto201605;2;3;;customer00;;42430;1\r\n" + 
			"auto201605k;1;3;SUPPLIER00;;1;42430;1\r\n" + 
			"auto201607;2;3;;customer00;;42461;1\r\n" + 
			"auto201607k;1;3;SUPPLIER00;;1;42461;1\r\n" + 
			"auto201609;2;3;;customer00;;42491;1\r\n" + 
			"auto201609k;1;3;SUPPLIER00;;1;42491;1\r\n" + 
			"auto201611;2;3;;customer00;;42522;1\r\n" + 
			"auto201611k;1;3;SUPPLIER00;;1;42522;1\r\n" + 
			"auto201613;2;3;;customer00;;42552;1\r\n" + 
			"auto201613k;1;3;SUPPLIER00;;1;42552;1\r\n" + 
			"auto201615;2;3;;customer00;;42583;1\r\n" + 
			"auto201615k;1;3;SUPPLIER00;;1;42583;1\r\n" + 
			"auto201617;2;3;;customer00;;42614;1\r\n" + 
			"auto201617k;1;3;SUPPLIER00;;1;42614;1\r\n" + 
			"auto201619;2;3;;customer00;;42644;1\r\n" + 
			"auto201619k;1;3;SUPPLIER00;;1;42644;1\r\n" + 
			"auto201621;2;3;;customer00;;42675;1\r\n" + 
			"auto201621k;1;3;SUPPLIER00;;1;42675;1\r\n" + 
			"auto201623;2;3;;customer00;;42705;1\r\n" + 
			"auto201623k;1;3;SUPPLIER00;;1;42705;1\r\n" + 
			"auto2019;1;3;SUPPLIER00;;1;42948;1\r\n" + 
			"auto2020;2;3;;customer00;;42948;1\r\n" + 
			"auto2117;1;3;SUPPLIER00;;1;42917;1\r\n" + 
			"auto2118;2;3;;customer00;;42917;1\r\n" + 
			"auto3218;1;3;SUPPLIER00;;1;42767;1\r\n" + 
			"auto3219;2;3;;customer00;;42767;1\r\n" + 
			"auto3313;1;3;SUPPLIER00;;1;42826;1\r\n" + 
			"auto3314;2;3;;customer00;;42826;1\r\n" + 
			"auto3613;1;3;SUPPLIER00;;1;43040;1\r\n" + 
			"auto3614;2;3;;customer00;;43040;1\r\n" + 
			"auto4254;1;3;SUPPLIER00;;1;42736;1\r\n" + 
			"auto4255;2;3;;customer00;;42736;1\r\n" + 
			"auto6301;1;3;SUPPLIER00;;1;43070;1\r\n" + 
			"auto6302;2;3;;customer00;;43070;1\r\n" + 
			"auto6634;1;3;SUPPLIER00;;1;43009;1\r\n" + 
			"auto6635;2;3;;customer00;;43009;1\r\n" + 
			"auto6936;1;3;SUPPLIER00;;1;42979;1\r\n" + 
			"auto6937;2;3;;customer00;;42979;1\r\n" + 
			"auto7368;1;3;SUPPLIER00;;1;42795;1\r\n" + 
			"auto7369;2;3;;customer00;;42795;1\r\n" + 
			"auto8325;1;3;SUPPLIER00;;1;42856;1\r\n" + 
			"auto8326;2;3;;customer00;;42856;1\r\n" + 
			"AZaus31KwK08g;1;2;SUPPLIER11;;1;43416;1\r\n" + 
			"B12dsZ4yw7J7g;2;2;;QSc3uss5t0m4r;;43408;1\r\n" + 
			"BcK2s0j1wCG2g;1;2;SUPPLIER11;;1;43464;1\r\n" + 
			"bEH2slvIwuDqg;2;2;;1mc5u9s1t0mtr;;43406;1\r\n" + 
			"cH1Psn96wm45g;2;2;;lCc8uosUtDmWr;;43416;2\r\n" + 
			"cmuasy5JwmUXg;2;2;;lCc8uosUtDmWr;;43425;12\r\n" + 
			"DBxFs0PRwPJug;1;2;SUPPLIER3;;1;43348;1\r\n" + 
			"DCVZsAH1wp89g;1;2;SUPPLIER5;;1;43414;1\r\n" + 
			"dTY5sVfMwe5dg;1;3;SUPPLIER4;;1;41520;1\r\n" + 
			"e23jsm31wUNrg;1;2;SUPPLIER4;;1;43344;1\r\n" + 
			"E429skxTw69zg;2;2;;2Ec6u4sgt7mvr;;43416;1\r\n" + 
			"eC3GshCpwFl6g;2;3;;ktc5ulsttcm9r;;43344;1\r\n" + 
			"El6KsMntwtGBg;2;2;;;6;43416;1\r\n" + 
			"eLyes9B9wUFbg;1;3;SUPPLIER3;;1;43346;1\r\n" + 
			"EWQ7sHviwMq1g;2;2;;1Rc0uRs4tFm0r;;43414;1\r\n" + 
			"F1Nss0rnw0bWg;1;2;SUPPLIER8;;1;43406;1\r\n" + 
			"F6IOsq6EwFl8g;2;3;;J5c3uTsMtbmKr;;43346;4\r\n" + 
			"f8yWs7j9w4jQg;2;2;;72cxuasntVmjr;;43414;1\r\n" + 
			"fBo3s862w5Fxg;2;3;;4jcvu2s5tFm6r;;43362;4\r\n" + 
			"FkK2s1BHwy8ag;2;3;;XXc1uzsAt1mer;;43344;1\r\n" + 
			"G0K4s20jw5zwg;2;2;;D0cXursstbmxr;;43410;1\r\n" + 
			"Ge0lsi6qwvp5g;2;2;;h3cyuGs2tRmTr;;43464;1\r\n" + 
			"gG7RsVkyw4M5g;2;2;;lCc8uosUtDmWr;;43401;1\r\n" + 
			"GHUOspb3w4vTg;1;2;SUPPLIER5;;1;43406;1\r\n" + 
			"H3j6sjrBwuGfg;2;2;;;15;43411;1\r\n" + 
			"hlxVstzqwxX2g;2;2;;sIc9uFsitymVr;;43131;1\r\n" + 
			"HT9MsI2cwz4kg;1;2;SUPPLIER2;;1;43426;1\r\n" + 
			"HuDOs2Xdw767g;2;2;;8jcWuas2t4m0r;;43416;1\r\n" + 
			"I48CsSwFwN4ng;1;2;SUPPLIER5;;1;43538;1\r\n" + 
			"I5iOs2YPwG19g;1;2;SUPPLIER9;;1;43530;1\r\n" + 
			"I8Frse0DwOo4g;1;2;SUPPLIER6;;1;43373;1\r\n" + 
			"ia82sc6YwC4Pg;1;2;SUPPLIER5;;1;43101;1\r\n" + 
			"iOYjskgawAvAg;1;2;SUPPLIER3;;1;43437;1\r\n" + 
			"j603s88TwImzg;1;2;SUPPLIER4;;1;43406;1\r\n" + 
			"Jiwus1J5wbE3g;1;2;SUPPLIER3;;1;43344;1\r\n" + 
			"k0fgskY8wpQ8g;2;3;;5vczuGsitPmdr;;43346;1\r\n" + 
			"K53us07UwFFWg;2;3;;CXc0uKsbtpm5r;;43345;1\r\n" + 
			"k6c0s4qTwbe3g;1;2;SUPPLIER9;;1;43410;1\r\n" + 
			"k9vnsFoxwinig;2;2;;n1cIu0sstfmcr;;43406;1\r\n" + 
			"kHorsDV0wxUOg;2;2;;sIc9uFsitymVr;;43418;6\r\n" + 
			"kRPas2pNwO6yg;1;2;SUPPLIER7;;1;43372;1\r\n" + 
			"ky64s824w7NWg;2;2;;LYcNu3s2tEmrr;;43129;1\r\n" + 
			"KZRcsh1zwIIgg;2;2;;O6cXu3stthm8r;;43425;1\r\n" + 
			"L102sl9Hw898g;2;3;;y6c3uQsxtdmGr;;43344;1\r\n" + 
			"l2d6skm2wEMzg;1;2;SUPPLIER7;;1;43131;1\r\n" + 
			"La33siedws54g;2;2;;XXc1uzsAt1mer;;43401;1\r\n" + 
			"LF5Oss8ow2q4g;2;2;;TjcNu3s7tNmgr;;43377;1\r\n" + 
			"lFBFs10dwE9Sg;1;2;SUPPLIER2;;1;43425;1\r\n" + 
			"lH7ts1UawuoFg;2;2;;4wcZu7s1tOmor;;43369;1\r\n" + 
			"LoiFsO61wZ71g;2;2;;Q7coulsut0mgr;;43477;1\r\n" + 
			"LoN0s6RNwz22g;2;2;;SGceuusetImPr;;43368;1\r\n" + 
			"m35Cs437weGWg;1;2;SUPPLIER2;;1;43414;1\r\n" + 
			"MLFdsxFjwX77g;2;3;;8jcWuas2t4m0r;;43346;3\r\n" + 
			"mm86sDk7wrj8g;1;2;SUPPLIER6;;1;43131;1\r\n" + 
			"mnv8sa1lw1M4g;1;2;SUPPLIER3;;1;43380;1\r\n" + 
			"muuFs7w2w56yg;1;2;SUPPLIER2;;1;43530;1\r\n" + 
			"mxz8sz79wVnPg;2;2;;bCcZups9twmgr;;43416;15\r\n" + 
			"MZFOsB09w0T1g;2;3;;;3;43414;1\r\n" + 
			"N04usU1Lwh8fg;2;3;;n1c0ujsFtMm0r;;43404;1\r\n" + 
			"nd2as06WwvH4g;1;2;SUPPLIER6;;1;43425;1\r\n" + 
			"np7AsphhwF9yg;1;2;SUPPLIER6;;1;43369;1\r\n" + 
			"NqG3s3n5wsTyg;1;2;SUPPLIER00;;1;43477;1\r\n" + 
			"nQr0sTdFwk6Qg;2;2;;72cxuasntVmjr;;43412;1\r\n" + 
			"nw7lsb6kwBQeg;2;2;;i7cLuEsot2m4r;;43373;1\r\n" + 
			"O57zsaqmwQd6g;2;2;;9Hcku4sztfmWr;;43425;17\r\n" + 
			"oCIzssv8wVohg;1;2;SUPPLIER3;;1;43373;1\r\n" + 
			"oHrZsoTXwR76g;2;3;;Oqc1u6smtXmir;;43345;1\r\n" + 
			"OJ2dsixBwIYXg;3;2;SUPPLIER6;;;43374;1\r\n" + 
			"Oq29sSV9wd5gg;3;2;SUPPLIER6;;;43373;1\r\n" + 
			"oR22sP48wXhYg;2;3;;lQcNups2t1mZr;;43346;1\r\n" + 
			"p28OsoTaw2DNg;2;2;;1ncou6sPtJm6r;;43404;1\r\n" + 
			"p2wQs228wOt9g;1;2;SUPPLIER10;;1;43416;1\r\n" + 
			"p67isZvZw4PPg;2;2;;09cxuqsFt6mXr;;43373;1\r\n" + 
			"pL6rsltWwQstg;2;2;;ysc2uGsct8mkr;;43412;1\r\n" + 
			"PTG7s9BawFvUg;2;2;;cGc5uEswtcm7r;;43405;1\r\n" + 
			"Py9rsrs7wYU8g;2;2;;vqc5u8s6tkmNr;;43377;1\r\n" + 
			"q66vsi3rwWIVg;2;3;;f7cyu2sytdm2r;;43347;1\r\n" + 
			"qJnJsmMawa7gg;1;2;SUPPLIER7;;1;43414;1\r\n" + 
			"qn9fsQ1fwqgUg;2;2;;SGceuusetImPr;;43360;1\r\n" + 
			"Qso1stlgwZt4g;2;2;;h8cKucsAtCmpr;;43541;1\r\n" + 
			"RibasmR2wF55g;2;2;;;21;43373;1\r\n" + 
			"Rn0JsjYtwZ2ug;2;2;;nhc0u7sft9m7r;;43408;1\r\n" + 
			"S3bR95U61W0jENWN4GGwk;2;2;;;2;43565;1\r\n" + 
			"Sb6EUUH35AkPT30;1;2;SUPPLIER4;;1;43689;1\r\n" + 
			"Sf8R34UF1W1qE5aNg0G7Q;2;2;;1gc8uCs3t8mTr;;43556;2\r\n" + 
			"SgwR6NU2JWjGEHPN0WGCE;2;2;;m6cOumsotGm8r;;43564;2\r\n" + 
			"sJxFsO6VwOk0g;2;2;;89cyuesltcm7r;;43416;5\r\n" + 
			"Sq3ROfUEgW2eEj5NY0Glf;2;2;;bCcZups9twmgr;;43565;2\r\n" + 
			"St6Pssduw5o6g;2;3;;;3;43346;1\r\n" + 
			"su8bsI9vwRaOg;1;2;SUPPLIER3;;1;43404;1\r\n" + 
			"SViR9wU9wWdbEB3N2MGLS;2;2;;sIc9uFsitymVr;;43564;2\r\n" + 
			"t303srBzwIlqg;2;2;;k3chuTsJtfmDr;;43416;1\r\n" + 
			"T96MsT1awIw2g;1;2;SUPPLIER5;;1;43425;1\r\n" + 
			"tm63syfJwEGTg;1;2;SUPPLIER7;;1;43418;1\r\n" + 
			"tMCXs8ICw7b1g;2;2;;m1ciuHs7tLmfr;;43382;1\r\n" + 
			"U9N7sixLwVmyg;1;2;SUPPLIER4;;1;43406;1\r\n" + 
			"UfqZsh0EwYEig;1;2;SUPPLIER6;;1;43412;1\r\n" + 
			"Umd7sW50w0B1g;2;2;;r2cmuns4tSmLr;;43373;1\r\n" + 
			"vHxvsaX4w7Bjg;2;2;;klcDuhszt3mZr;;43373;1\r\n" + 
			"w0bFsYe1w0T7g;1;2;SUPPLIER6;;1;43447;1\r\n" + 
			"W2uesoHMw60mg;2;3;;;4;43348;1\r\n" + 
			"wx50sUd2wbOig;2;2;;;8;43416;1\r\n" + 
			"X1BpsPqEw8ggg;1;2;SUPPLIER8;;1;43407;1\r\n" + 
			"X54zsQoPwGhIg;2;2;;3tcbuKsxtymgr;;43407;1\r\n" + 
			"XBK1sRE5w938g;2;2;;R0c4u5sEtkmFr;;43117;1\r\n" + 
			"xPocsZXgw23Kg;2;2;;I7caursLtLm7r;;43426;1\r\n" + 
			"XPwFs121ww2ag;1;2;SUPPLIER3;;1;43414;1\r\n" + 
			"Xy41sVRdws62g;1;2;SUPPLIER2;;1;43419;1\r\n" + 
			"y19Es38ewSl5g;2;2;;wzc9uis0tFm1r;;43373;1\r\n" + 
			"Y63Rsbd3whw2g;2;2;;J2cmu0s9t0m6r;;43404;1\r\n" + 
			"ypFMsIm7w307g;2;3;;41cVuHs1tUmcr;;43346;1\r\n" + 
			"z4Q1s2PPwDdWg;1;2;SUPPLIER3;;1;43541;1\r\n" + 
			"zr79sH7Mwdpug;2;2;;;12;43424;1\r\n" + 
			"zuqtsIE2wEF1g;2;2;;f7cyu2sytdm2r;;43410;1";

	static void setAdjustedDateMap()   {
		if (null != adjustedDates) return;
		adjustedDates = new HashMap<>();
		String[] rawDates = MAPPED_DATES.split("\r\n");
		for (int i = 0; i < rawDates.length; i++) {
			String[] codeAndDate = rawDates[i].split(";");
			try {
				adjustedDates.put(codeAndDate[0], simpleDateFormat.parse(codeAndDate[1]));
			} catch (ParseException e) {
				 
				e.printStackTrace();
			}
		}
	}
	
	static Date getTransactionDate(String code) {
		setAdjustedDateMap();
		return adjustedDates.get(code);
	}
}
