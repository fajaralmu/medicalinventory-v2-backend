package com.pkm.medicalinventory.externalapp;

import java.util.ArrayList;
import java.util.List;

import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.entity.Unit;

public class MedsList {
	
	static final String medList = "AB151;ABOCATH NO 18;9;1\r\n" + 
			"AB152;ABOCATH NO 20;3;1\r\n" + 
			"AB153;ABOCATH NO 22;3;1\r\n" + 
			"AB154;ABOCATH NO 24;4;1\r\n" + 
			"AC2;ACYCLOVIR Tab;2;0\r\n" + 
			"AC3;ACYCLOVIR CREAM;3;0\r\n" + 
			"AL1;ALOPURINOL;2;0\r\n" + 
			"AL143;ALAT SUNTIK 1 ML;3;1\r\n" + 
			"AL144;ALAT SUNTIK 25 ML;4;1\r\n" + 
			"AL145;ALAT SUNTIK 3 ML;4;1\r\n" + 
			"AL146;ALAT SUNTIK 5 ML;4;1\r\n" + 
			"AL147;ALAT SUNTIK 10 ML;4;1\r\n" + 
			"AL149;ALBOTHYL;3;0\r\n" + 
			"AL18;ALBENDAZOLE;2;0\r\n" + 
			"AM10;AMBOXOL 30 MG;2;0\r\n" + 
			"AM11;AMBROXOL SYR;3;0\r\n" + 
			"AM23;AMITRIpTILLIN 25 mg;2;0\r\n" + 
			"AM4;AMINOFILINA 200 ;2;0\r\n" + 
			"AM5;AMIN0FILINA INJ;2;0\r\n" + 
			"AM6;AMOKSISILIN KAPSUL 500 MG;7;0\r\n" + 
			"AM7;AMOKSISILIN SYRUP ;3;0\r\n" + 
			"AM8;AMLODIPIN 5 MG;2;0\r\n" + 
			"AM9;AMLODIPIN 10 MG;2;0\r\n" + 
			"AN12;ANTALGIN TABLET 500 MG;2;0\r\n" + 
			"AN13;ANTASID SYR;3;0\r\n" + 
			"AN14;ANTASIDA DOEN TAMBLET KOMBINASI;2;0\r\n" + 
			"AN15;ANTIBAKTERI DOEN SALP (BACITRACIN ;8;0\r\n" + 
			"AN19;ANTIFUNGI DOEN;2;0\r\n" + 
			"AN20;ANTIHEMOROID;2;0\r\n" + 
			"AN21;ANTIMIGREN;2;0\r\n" + 
			"AN22;ANATON;2;0\r\n" + 
			"AQ150;AQUA PRO INJ;3;0\r\n" + 
			"AS141;ASEPTIC GEL;7;0\r\n" + 
			"AS142;ASEPTIK ISI ULANG;2;0\r\n" + 
			"AS148;ASAM ASETAT;3;0\r\n" + 
			"AS16;ASAM ASCORBAT (VIT C) TABLET 50 MG;2;0\r\n" + 
			"AS17;ASAM MEFENAMAT 500 MG;2;0\r\n" + 
			"AS24;ASAM TRANEXAMAT 500 MG;2;0\r\n" + 
			"BA158;BAYCLIN;3;0\r\n" + 
			"BA26;BABYCOUGH;3;0\r\n" + 
			"BE25;BETAHISTIN;2;0\r\n" + 
			"BE27;BETAMETASON;2;0\r\n" + 
			"BI157;BISTURI;2;1\r\n" + 
			"BI28;BISMECON;2;0\r\n" + 
			"BI29;BISAKODIL Supp;2;0\r\n" + 
			"BL156;BLOOD LANCET;2;1\r\n" + 
			"BR155;BRACHED ABS;2;0\r\n" + 
			"CA131;CAVIPLEK SYRUP;3;0\r\n" + 
			"CA160;CAVIT;2;0\r\n" + 
			"CA161;CAT GUT CHROMIX 2.0;2;0\r\n" + 
			"CA162;CAT GUT CHROMIX 3.0;2;0\r\n" + 
			"CA163;CAT GUT PLAIN 2.0;2;0\r\n" + 
			"CA164;CAT GUT PLAIN 3.0;2;0\r\n" + 
			"CA166;CANUL NASAL BAYI;2;0\r\n" + 
			"CA167;CANUL NASAL ANAK;2;1\r\n" + 
			"CA168;CANUL NASAL DWS;2;0\r\n" + 
			"CA31;CA GKUKONS;2;0\r\n" + 
			"CA35;CAPTOPRIL 12.5 MG;2;0\r\n" + 
			"CA36;CAPTOPRIL 25 MG;2;0\r\n" + 
			"CE32;CETIRIZIN;2;0\r\n" + 
			"CE33;CEFADROXIL;2;0\r\n" + 
			"CH165;CHKM;2;0\r\n" + 
			"CH343;CHROMIX;2;0\r\n" + 
			"CL30;CLOZAPIN 25 MG;2;0\r\n" + 
			"CO37;COREDRIL;2;0\r\n" + 
			"Contoh11;contoh obat;6;0\r\n" + 
			"CR159;CRESOPHEN;2;0\r\n" + 
			"CT34;CTM;2;0\r\n" + 
			"DE169;DERMAFIT;2;0\r\n" + 
			"DE170;DEVITALISASI PASTA;7;0\r\n" + 
			"DE239;DEKSTROMETORFAN HBR TABLET ;2;0\r\n" + 
			"DE38;DEKSAMETASON TABLET 05 MG;2;0\r\n" + 
			"DE39;DEKSAMETHASONE INJ;2;0\r\n" + 
			"DI40;DIGOKSIN 025 ML;2;0\r\n" + 
			"DI41;DIMENHIDRINAT 200 MG;2;0\r\n" + 
			"DI43;DIAZEPAM 2 MG;2;0\r\n" + 
			"DO42;DOMPERIDONE TAB;2;0\r\n" + 
			"ef226;efinefrine;2;0\r\n" + 
			"EN46;ENBATIC;2;0\r\n" + 
			"EP44;EPINEFRIN(ADRENALIN) INJ;2;0\r\n" + 
			"ER45;ERGOTAMIN;2;0\r\n" + 
			"ET171;ETIL CHLORIDE;2;0\r\n" + 
			"ET172;ETANOL;3;0\r\n" + 
			"EU173;EUGENOL;3;0\r\n" + 
			"FE48;FENOL GLISEROL TETES TELINGA 10 percent;3;0\r\n" + 
			"FE50;FENOBARBITAL 30 mg;2;0\r\n" + 
			"FI51;FITOMENADION (VIT K1) 10 MG;2;0\r\n" + 
			"FI52;FITOMENADION (VIT K1) INJ 2 mg/ml;2;0\r\n" + 
			"FL49;FLUNARIZIN;2;0\r\n" + 
			"FU47;FUROSEMID 40 MG;2;0\r\n" + 
			"GA55;GARAM ORALIT UTK 200 ML AIR;10;0\r\n" + 
			"GE179;GELANG BUMIL;2;0\r\n" + 
			"GE53;GENOINT SM;2;0\r\n" + 
			"GE54;GENTAMICIN SM;2;0\r\n" + 
			"GE57;GENTIAN VIOLET LARUTAN 1 percent;3;0\r\n" + 
			"GL174;GLOVES S;2;0\r\n" + 
			"GL175;GLOVES M;2;0\r\n" + 
			"GL176;GLOVES L;2;0\r\n" + 
			"GL177;GLOVES GYNEKOLOGI 6.5;2;0\r\n" + 
			"GL178;GLOVES GYNEKOLOGI 7;2;0\r\n" + 
			"GL180;GLASS IONOMER CEMENT (GC IX);2;0\r\n" + 
			"GL58;GLISERIL GUAIKOLAT TABLET 100 MG;2;0\r\n" + 
			"GL59;GLIBENKLAMIDA TABLET 5 MG;2;0\r\n" + 
			"GL61;GLUKOSA 5 pecent;3;0\r\n" + 
			"GL62;GLUKOSA 40 percent;3;0\r\n" + 
			"GO56;GOM;2;0\r\n" + 
			"GR60;GRISEUVULVIN TAB;2;0\r\n" + 
			"HA340;HALLOPERIDOL 0.5 MG;2;0\r\n" + 
			"HA63;HALLOPERIDOL 5 mg;2;0\r\n" + 
			"HA64;HALOPERIDOL 15 MG;2;0\r\n" + 
			"HA65;HALOPERIDOL INJ;2;0\r\n" + 
			"HC181;HCL 01 N;2;0\r\n" + 
			"HE67;HEMAFORT;2;0\r\n" + 
			"HI66;HIDROKLOROTIAZIDA (HCT) TBLT 25 MG;2;0\r\n" + 
			"HI68;HIDROKORTISONE KREM 25 percent;10;0\r\n" + 
			"HY182;HYDYCAL;2;0\r\n" + 
			"IB69;IBUPROFEN 400 MG;2;0\r\n" + 
			"IN183;INFUSET ANAK;2;0\r\n" + 
			"IN184;INFUSET DEWASA;2;0\r\n" + 
			"IS70;ISDN;2;0\r\n" + 
			"JA186;JARUM KULIT NO 14;2;0\r\n" + 
			"JA187;JARUM  KULIT  15;2;0\r\n" + 
			"JA344;JARUM JAHIT NO 9;2;0\r\n" + 
			"JA345;JARUM JAHIT NO 14;2;1\r\n" + 
			"JA346;JARUM JAHIT 15;2;0\r\n" + 
			"JE185;JELLY DOPLLER;2;0\r\n" + 
			"KA188;KACA SLIDE/OBJEC GLASS;2;1\r\n" + 
			"KA189;KAPAS;2;0\r\n" + 
			"KA190;KASA 40/40 STERIL ;2;0\r\n" + 
			"KA191;KASA 2X80 CM;2;0\r\n" + 
			"KA192;KASA 4X15 CM;2;0\r\n" + 
			"KA193;KASA 4X5CM;5;0\r\n" + 
			"KA194;KASA 4X3 CM;2;0\r\n" + 
			"KA71;KALSIUM LAKTAT (KALK) TABLET 500 MG;2;0\r\n" + 
			"KE80;KETOKONAZOLE TAB;2;0\r\n" + 
			"KE81;KETOKONAZOL cream;10;0\r\n" + 
			"KL72;KLORAMFENIKOL TETES MATA;2;0\r\n" + 
			"KL73;KLORAMFENIKOL TETES TELINGA 3 percent;3;0\r\n" + 
			"KL74;KLORAMFENIKOL SALEP MATA 1 percent;7;0\r\n" + 
			"KL75;KLORAMFENICOL SYRUP;3;0\r\n" + 
			"KL76;KLORAMFENICOL SK;2;0\r\n" + 
			"KL77;KLORAMFENIKOL KAPSUL 250 MG;7;0\r\n" + 
			"KL82;KLORPROMAZIN 100 MG;2;0\r\n" + 
			"KO78;KOTRIMOKSAZOLE 480;2;0\r\n" + 
			"KO79;KOTRIMOKSAZOLE SYR;3;0\r\n" + 
			"LA84;LACTO B;2;0\r\n" + 
			"LI195;LIDOKAIN INJ 2;2;0\r\n" + 
			"LI196;LISOL;3;1\r\n" + 
			"LI83;LIDOKAIN INJ;2;0\r\n" + 
			"MA198;MASKER;2;0\r\n" + 
			"ME341;METOKLOPRAMID TAB;2;0\r\n" + 
			"ME87;METILERGOMETRIN MALEAT 0.125 MG;2;0\r\n" + 
			"ME88;METILERGOMETRIN MALEAT INJ 0.2 mg/ml;2;0\r\n" + 
			"ME89;METRONODAZOLE TABLET 250 MG;2;0\r\n" + 
			"ME90;METRONODAZOLE TABLET 500 MG;2;0\r\n" + 
			"ME92;METFORMIN 500 mg;2;0\r\n" + 
			"ME94;MEBENDASOL 100 MG;2;0\r\n" + 
			"MG85;MGSO4 40 pecent;6;0\r\n" + 
			"MI199;MINYAK EMERSI;2;0\r\n" + 
			"MI91;MICONAZOL;2;0\r\n" + 
			"MO86;MOLANEURON;2;0\r\n" + 
			"MO93;MOLEXFLU;2;0\r\n" + 
			"MU197;MUMMYING PASTA;2;0\r\n" + 
			"mu235;mucus extractor;2;0\r\n" + 
			"NA200;NACL INFUSE;3;0\r\n" + 
			"NA95;NACL INFUSE;3;0\r\n" + 
			"NA98;NATRIUM DIKLOFENAC 50 MG;2;0\r\n" + 
			"NE97;NEO KAOMINAL;2;0\r\n" + 
			"NI96;NIFEDIPINE;2;0\r\n" + 
			"OA100;OAT DWS KAT II;2;0\r\n" + 
			"OA101;OAT ANAK;2;0\r\n" + 
			"OA99;OAT DWS KAT I;2;0\r\n" + 
			"OB102;OBAT KUSTA;2;0\r\n" + 
			"OB103;OBAT BATUK HITAM (OBH) ITRA;2;0\r\n" + 
			"OB104;OBH GENERIK;2;0\r\n" + 
			"OB342;OBAT BATUK HITAM (OBH) CAIRAN;2;0\r\n" + 
			"OBH342;OBAT BATUK HITAM (OBH) CAIRAN;2;0\r\n" + 
			"OK105;OKSITETRASIKLINA HCL SALEP MATA ;2;0\r\n" + 
			"OK106;OKSITETRASIKLIN SK;2;0\r\n" + 
			"OK107;OKSITOSIN INJ;2;0\r\n" + 
			"OM108;OMEPRAZOLE;2;0\r\n" + 
			"PA109;PARACETAMOL SIRUP 120 MG/5 ML;2;0\r\n" + 
			"PA110;PARACETAMOL TABLET 500 MG;2;0\r\n" + 
			"PA201;PASTA DEVITALISASI (NON ARSEN);2;0\r\n" + 
			"PE115;PEHAFRAL;2;0\r\n" + 
			"PI111;PIRANTEL PAMOAT TAB. 125 MG BASA;2;0\r\n" + 
			"PI112;PIRIDOKSINA HCL ( VIT. B6 ) TAB. 10 MG;2;0\r\n" + 
			"PI113;PIROXICAM 20 MG;2;0\r\n" + 
			"PL202;PLESTER 5 YARD X 2 INCI;2;0\r\n" + 
			"PL206;PLAIN 20;2;0\r\n" + 
			"PL207;PLAIN 30;2;0\r\n" + 
			"PO203;POVIDON 30 ML;3;0\r\n" + 
			"PO204;POVIDON 300 ML;3;0\r\n" + 
			"PO205;POT SEPUTUM;6;0\r\n" + 
			"PR114;PREDNISON TAB. 5 MG;2;0\r\n" + 
			"PR116;PROPANOLOL;3;0\r\n" + 
			"RA119;RANITIDIN;2;0\r\n" + 
			"RE118;RESPERIDONE;2;0\r\n" + 
			"RE210;REAGENt hBsAg;2;0\r\n" + 
			"RE211;REAGEN ZEIHL NELSON;3;0\r\n" + 
			"RE212;REAGEN GOL DARAH;3;0\r\n" + 
			"RE213;REAGEN RAPID 1 HIV;2;0\r\n" + 
			"RL117;RL INFUSE;3;1\r\n" + 
			"RL209;RL INFUSE;3;1\r\n" + 
			"RO208;ROCKLES;3;0\r\n" + 
			"SA120;SALBUTAMOL 2 MG;2;0\r\n" + 
			"SA121;SALBUTAMOL 4 MG;7;0\r\n" + 
			"SA125;SALP 2-4;7;0\r\n" + 
			"SA127;SALISIL BEDAK;3;0\r\n" + 
			"SA232;SAFETY BOX 25 L;4;1\r\n" + 
			"SA233;SAFETY BOX 5 L;4;1\r\n" + 
			"sa236;sarung tangan industri m;5;1\r\n" + 
			"sa237;sarung tangan industri L;5;1\r\n" + 
			"SC128;SCABISID;2;0\r\n" + 
			"SE124;SELEDIAR/MOLAGIT;2;0\r\n" + 
			"SE215;SENSI GLOVES;5;1\r\n" + 
			"SH229;SHOLOSHOT 05 ML;2;0\r\n" + 
			"SH230;SHOLOSHOT 005 ML;2;0\r\n" + 
			"SI129;SIMVASTATIN;2;0\r\n" + 
			"SI216;SILK;2;0\r\n" + 
			"SL214;SLIDE BOX;4;1\r\n" + 
			"SO123;SOLAFLUZ;2;0\r\n" + 
			"SP126;SPASMECO;2;0\r\n" + 
			"SP231;SPUIT 5 ML;2;1\r\n" + 
			"ST217;STIK KOLESTEROL;6;1\r\n" + 
			"ST218;STIK GULA DARAH;3;1\r\n" + 
			"ST219;STIK ASAM URAT;2;1\r\n" + 
			"ST220;STIK PROTEIN URIN;2;1\r\n" + 
			"ST221;STIK HB;2;1\r\n" + 
			"SU122;SULFASETAMID TM;2;0\r\n" + 
			"TA234;TABUNG VCT;2;1\r\n" + 
			"TE223;TES KEHAMILAN;2;1\r\n" + 
			"TE224;TEMPORARY FLETCHER;2;1\r\n" + 
			"TH133;THIAMFENIKOL 500;2;0\r\n" + 
			"TR132;TRUVIT SYR;3;0\r\n" + 
			"TR134;TRIHEXIPHENIDIL;2;0\r\n" + 
			"TR222;TRANFUSI SET;3;1\r\n" + 
			"TY130;TYAMIN HCL (VIT. B1) TAB. 50 MG;2;0\r\n" + 
			"UL225;ULTRAFIK;2;0\r\n" + 
			"UM228;UMBILICAL CORD;2;1\r\n" + 
			"UN227;UNDERPAD;4;1\r\n" + 
			"ur238;urin bag;3;1\r\n" + 
			"VI135;VITAMIN K INJ;2;0\r\n" + 
			"VI136;VITAMIN K TAB;2;0\r\n" + 
			"VI137;VITAMIN B COMPLEK TAB.;2;0\r\n" + 
			"VI138;VITAMIN a 100.000 IU;2;0\r\n" + 
			"VI139;VITAMIN A 200.00 IU;2;0\r\n" + 
			"ZI140;ZINK;2;0\r\n" + 
			"";

	public static void main(String[] args) {
		
	}
	
	static List<Product> getProducts() {
		
		List<Product> products = new ArrayList<Product>();
		String[] props = medList.split("\r\n");
		for (int i = 0; i < props.length; i++) {
			String[] prop = props[i].split(";");
			Product product = Product.builder()
					.code(prop[0])
					.name(prop[1])
					.unit(getUnit(Long.valueOf(prop[2])))
					.description("Desc "+prop[1])
					.utilityTool(prop[3].equals("1")).build();
			products.add(product);
		}
		return products ;
	}

	private static Unit getUnit(Long valueOf) {
		Unit unit = new Unit();
		unit.setId(valueOf);
		// TODO Auto-generated method stub
		return unit ;
	}
}
