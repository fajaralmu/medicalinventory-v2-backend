package com.pkm.medicalinventory.service.transaction;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.pkm.medicalinventory.dto.InventoryData;
import com.pkm.medicalinventory.dto.PeriodicReviewResult;
import com.pkm.medicalinventory.entity.Configuration;
import com.pkm.medicalinventory.service.config.InventoryConfigurationService;
import com.pkm.medicalinventory.statistics.ItemTabelB;
import com.pkm.medicalinventory.statistics.ItemTabelFrek;
import com.pkm.medicalinventory.statistics.StatisticTables;
import com.pkm.medicalinventory.statistics.Tabelchi;
import com.pkm.medicalinventory.statistics.Tabelkolmogorov;
import com.pkm.medicalinventory.statistics.itemTabelKumulatif;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalculationUtil {

	@Autowired
	private StatisticTables statisticTables;
	@Autowired
	private InventoryConfigurationService inventoryConfigurationService;

	private static Random random = new Random();

	private static char[] charsLow = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
			'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	private static char[] charsHi = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	private static int[] charNum = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };

	public static Integer statusKadaluarsa(Date kadaluarsa) {
		Integer sts = 1;
		Date d = new Date();
		java.sql.Date now = new java.sql.Date(d.getTime());
		long diff = kadaluarsa.getTime() - now.getTime();
		long diffDays = diff / (24 * 60 * 60 * 1000);
		if (diffDays <= 30 * 6 && diffDays > 0) {
			sts = 0;
		} else if (diffDays <= 0) {
			sts = -1;
		}
		return sts;
	}

	public PeriodicReviewResult hitungPeriodicReview(Double demand, Double stdev, double time, double lTime,
			Integer stockOnHand) {

		Double Z = 1.64;
		Double safetyStock = Z * stdev * Math.sqrt((time + lTime) / time);
		Double targetStockLevel = demand * ((time + lTime) / time) + safetyStock;
		Double orderSize = targetStockLevel - stockOnHand;
		System.out.println(Z + " " + stdev + " " + 1 /* time / time */ + " " + lTime / time);
		System.out.println(demand + " " + safetyStock);

		return PeriodicReviewResult.builder().orderSize(orderSize).safetyStock(safetyStock)
				.targetStockLevel(targetStockLevel).build();
	}

	public PeriodicReviewResult periodicReview(Integer stock, List<InventoryData> usageData, boolean ujiNormal) {
		Integer[] pemakaian = new Integer[usageData.size()];
		
		for (int i = 0; i < usageData.size(); i++) {
			usageData.get(i).checkNulls();
			pemakaian[i] = usageData.get(i).getTotalItemsSum();
		}

		if (ujiNormal) {
			if (pemakaian.length > 30 && maxInt(pemakaian) > 10) {
				if (!isNormalChiSQR(pemakaian, 0.05))
					return PeriodicReviewResult.builder().description("Failed isNormalChiSQR").build();
			} else {
				if (!isNormalKolmogorov(pemakaian, 0.05))
					return PeriodicReviewResult.builder().description("Failed isNormalKolmogorov").build();
			}
		}

		Double[] meanstd = getMEANSTDEV(pemakaian, false);
		Double stdev = meanstd[1];
		Double mean = meanstd[0];
		System.out.println(stdev + " " + mean);
		Configuration k = inventoryConfigurationService.getTempConfiguration();
		PeriodicReviewResult prediksiPemesanan = hitungPeriodicReview(mean, stdev, k.getCycleTime(), k.getLeadTime(), stock);

		return prediksiPemesanan;
	}

//	public static List<Product> urutkanObatModelABC(List<Product> daftarObat) {
//		List<Product> obatBerurut = new ArrayList<>();
//		Integer nilaiPemakaianObat = 0;
//		Integer kumulatif = 0;
//		Integer hargaSatuan = 0, jmlPemakaianObat = 0;
//
//		for (Product o : daftarObat) {
//			Product obt = new Product();
//			loop: for (Product ob : daftarObat) {
//				jmlPemakaianObat = ob.get_Pemakaian();
//				hargaSatuan = ob.get_Hargasatuan();
//				if (jmlPemakaianObat * hargaSatuan >= nilaiPemakaianObat) {
//					for (Product ot : obatBerurut) {
//						if (ob.getId().equals(ot.getId())) {
//							continue loop;
//						}
//					}
//					obt = ob;
//					nilaiPemakaianObat = jmlPemakaianObat * ob.get_Hargasatuan();
//				}
//			}
//			kumulatif += nilaiPemakaianObat;
//			obt.set_Kumulatifpakai(kumulatif);
//			obatBerurut.add(obt);
//			nilaiPemakaianObat = 0;
//		}
//		return obatBerurut;
//	}

	private static Integer maxInt(Integer[] list) {
		if (null == list) return 0;
		int max = 0;
		for (int i = 0; i < list.length; i++) {
			if (list[i] > max)
				max = list[i];
		}
		return max;
	}

	private static Double maxDouble(Double[] list) {
		double max = list[0];
		for (int i = 0; i < list.length; i++) {
			if (list[i] > max)
				max = list[i];
		}
		return max;
	}

	public static Integer[] urutkanList(Integer[] list_int) {
		Integer[] list_urut = new Integer[list_int.length];

		Map<Integer, Integer> map_int = new HashMap<Integer, Integer>();
		Map<Integer, Integer> map_int_urut = new HashMap<Integer, Integer>();
		Map<Integer, Integer> map_int_urut_final = new HashMap<Integer, Integer>();
		System.out.println(list_int.length);

		int min = maxInt(list_int);
		int max = list_int[0];
		int key = 0;
		int key_map_urut = 0;
		// System.out.println(min);
		for (int i = 0; i < list_int.length; i++) {
			map_int.put(i, list_int[i]);
			// System.out.print(list_int[i] + ",");
		}

		for (int i = 0; i < map_int.size(); i++) {
			loop: for (int j = 0; j < map_int.size(); j++) {
				if (map_int_urut.containsKey(j))
					continue;

				if (map_int.get(j) < min) {
					min = map_int.get(j);
					key = j;

				} else
					continue loop;
			}

			map_int_urut_final.put(key_map_urut, min);
			map_int_urut.put(key, min);
			key_map_urut++;
			// System.out.println("Added"+map_int_urut.get(key));
			min = maxInt(list_int);
		}

		for (int i = 0; i < map_int_urut_final.size(); i++) {
			list_urut[i] = map_int_urut_final.get(i);
		}
		return list_urut;
	}

//	public static Object maxObj(Object[] list) {
//		Object max = list[0];
//		if (max.getClass().equals(String.class))
//			max = ((String) list[0]).toLowerCase();
//		else if (max.getClass().equals(Integer.class) || max.getClass().equals(Double.class)
//				|| max.getClass().equals(java.sql.Date.class) || max.getClass().equals(DrugStock.class))
//			max = list[0];
//
//		for (int i = 0; i < list.length; i++) {
//			if (max.getClass().equals(String.class)) {
//				if (((String) list[i]).toLowerCase().compareTo((String) max) > 0)
//					max = list[i];
//			} else if (max.getClass().equals(Integer.class)) {
//				if (((Integer) list[i]) > ((Integer) max))
//					max = list[i];
//			} else if (max.getClass().equals(Double.class)) {
//				if (((Double) list[i]) > ((Double) max))
//					max = list[i];
//			} else if (max.getClass().equals(java.sql.Date.class)) {
//				if (((java.sql.Date) list[i]).compareTo(((java.sql.Date) max)) > 0)
//					max = list[i];
//			} else if (max.getClass().equals(DrugStock.class)) {
//				if (((DrugStock) list[i]).getAliranobat().getKadaluarsa().compareTo(((DrugStock) max).getAliranobat().getKadaluarsa()) > 0)
//					max = list[i];
//			}
//		}
//		// max.comp
//		return max;
//	}

//	public static Object[] urutkanListObj(Object[] list_obj) {
//		Object[] list_urut = new Object[list_obj.length];
//
//		Map<Integer, Object> map_obj = new HashMap<Integer, Object>();
//		Map<Integer, Object> map_obj_urut = new HashMap<Integer, Object>();
//		Map<Integer, Object> map_obj_urut_final = new HashMap<Integer, Object>();
//		System.out.println(list_obj.length);
//
//		Object min = maxObj(list_obj);
//		int key = 0;
//		int key_map_urut = 0;
//		for (int i = 0; i < list_obj.length; i++) {
//			map_obj.put(i, list_obj[i]);
//			// System.out.print(list_int[i] + ",");
//		}
//
//		for (int i = 0; i < map_obj.size(); i++) {
//			loop: for (int j = 0; j < map_obj.size(); j++) {
//				if (map_obj_urut.containsKey(j))
//					continue;
//				if (map_obj.get(j).getClass().equals(String.class)) {
//					if (((String) map_obj.get(j)).toLowerCase().compareTo(((String) min).toLowerCase()) <= 0) {
//						min = map_obj.get(j);
//						key = j;
//					} else
//						continue loop;
//				} else if (map_obj.get(j).getClass().equals(Integer.class)) {
//					if (((Integer) map_obj.get(j)) <= ((Integer) min)) {
//						min = map_obj.get(j);
//						key = j;
//					} else
//						continue loop;
//				} else if (map_obj.get(j).getClass().equals(Double.class)) {
//					if (((Double) map_obj.get(j)) <= ((Double) min)) {
//						min = map_obj.get(j);
//						key = j;
//					} else
//						continue loop;
//				} else if (map_obj.get(j).getClass().equals(java.sql.Date.class)) {
//					if (((java.sql.Date) map_obj.get(j)).compareTo(((java.sql.Date) min)) <= 0) {
//						min = map_obj.get(j);
//						key = j;
//					} else
//						continue loop;
//				} else if (map_obj.get(j).getClass().equals(DrugStock.class)) {
//					if (((DrugStock) map_obj.get(j)).getAliranobat().getKadaluarsa()
//							.compareTo(((DrugStock) min).getAliranobat().getKadaluarsa()) <= 0) {
//						min = map_obj.get(j);
//						key = j;
//					} else
//						continue loop;
//				}
//			}
//
//			map_obj_urut_final.put(key_map_urut, min);
//			map_obj_urut.put(key, min);
//			key_map_urut++;
//			// System.out.println("Added"+map_int_urut.get(key));
//			min = maxObj(list_obj);
//		}
//
//		for (int i = 0; i < map_obj_urut_final.size(); i++) {
//			list_urut[i] = map_obj_urut_final.get(i);
//		}
//
//		System.out.println("oke. New List: ");
//		/*
//		 * for (int i = 0; i < list_urut.length; i++) System.out.print(list_urut[i] +
//		 * ","); System.out.println("--");
//		 */
//		return list_urut;
//	}

	public boolean isNormalChiSQR(Integer[] list, double signifikansi) {
		Integer[] list_int = urutkanList(list);

		List<ItemTabelFrek> tabelFrekuensi = new ArrayList<ItemTabelFrek>();
		int jangkauan = list_int[list_int.length - 1] - list_int[0];
		// System.out.println(list_int[list_int.length - 1]+"-"+list_int[0]);
		double n = list_int.length;
		double kelas = 1 + 3.3 * (Math.log(n) / Math.log(10));
		kelas = Math.ceil(kelas);
		double panjangkelas_d = Math.ceil(jangkauan / kelas);
		int panjangkelas = (int) panjangkelas_d;
		System.out.println("________START UJI CHI SQR_______");
		System.out.println("1. Jangkauan :" + jangkauan + " N: " + n);
		System.out.println("2. Kelas :" + kelas);
		System.out.println("3. Panjang Kelas(jangkauan/kelas) :" + panjangkelas);

		int atas = maxInt(list_int), bawah = atas - panjangkelas + 1;
		for (int i = 0; i < list_int.length; i++)
			System.out.print(list_int[i] + ",");

		System.out.println();
		// System.out.print(atas + "===");
		int frek = 0;
		boolean pindah = false;

		System.out.println(list_int.length);
		int idk = 1;
		for (int k = 1; k <= kelas; k++) {
			System.out.print(k + "->");
			System.out.print(bawah + " == ");
			for (int i = list_int.length - 1; i >= 0; i--) {
				int item = list_int[i];

				if (item >= bawah && item <= atas) {
					System.out.print(item + ",");
					frek++;
				} else {

				}
			}
			System.out.println("===" + atas + "  f:" + frek);
			tabelFrekuensi.add(new ItemTabelFrek(idk, atas, bawah, frek));
			frek = 0;
			idk++;
			atas = bawah - 1;
			bawah = bawah - panjangkelas;
		}

		System.out.println();
		for (int i = tabelFrekuensi.size() - 1; i >= 0; i--)
			System.out.println(tabelFrekuensi.get(i));
		// Lengkapi tabel
		int totalF = 0, totalF_Mid = 0, totalF_MidMid = 0;

		for (ItemTabelFrek i : tabelFrekuensi) {
			int up = i.getAtas(), down = i.getBawah(), f = i.getFrekuensi();
			int mid = down + (up - down) / 2;
			i.setMidPoint(mid);
			i.setFrek_MidPoint(f * mid);
			i.setMidPointKuadrat(mid * mid);
			i.setFrek_midPointKuadrat(f * i.getMidPointKuadrat());

			totalF += f;
			totalF_Mid += i.getFrek_MidPoint();
			totalF_MidMid += i.getFrek_MidPointKuadrat();
		}
		System.out.println("-------------BARU--------------");
		for (int i = tabelFrekuensi.size() - 1; i >= 0; i--)
			System.out.println(tabelFrekuensi.get(i));

		System.out.println("f:" + totalF + " ,fx:" + totalF_Mid + " ,fx^2:" + totalF_MidMid);

		if (totalF_Mid <= 0) {
			System.out.println("ERROR: totalF_Mid is <=0 " + totalF_Mid);
			totalF_Mid = 1;
		}
		double mean = totalF_Mid / (double) totalF;
		System.out.println("4. Rata-rata: " + totalF_Mid + "/" + totalF + "=" + mean);

		double fxPerf = totalF_Mid / (double) n;
		double stdev = Math.sqrt((totalF_MidMid / totalF) - (fxPerf * fxPerf));
		System.out.println("total_mid^2 : " + totalF_MidMid + " fxPerf: " + fxPerf);
		System.out.println("5. Stdev: " + stdev);
		double XHitung = 0.0;
		List<ItemTabelB> tabelB = new ArrayList<ItemTabelB>();
		for (ItemTabelFrek i : tabelFrekuensi) {
			ItemTabelB ib = new ItemTabelB(i);
			ib.setMEAN_CALC(mean);
			ib.setJUMLAHDATA(totalF);
			ib.setSTDEV(stdev);
			ib.hitung();
			XHitung += ib.getMean();
			tabelB.add(ib);
		}

		for (int i = tabelB.size() - 1; i >= 0; i--)
			System.out.println(tabelB.get(i));

		System.out.println("6. X Hitung: " + XHitung);

		/*
		 * 
		 * HITUNG TARAF NYATA
		 */

		int derajatBebas = (int) Math.round(kelas) - 3;
		System.out.println("7. DERAJAT BEBAS: " + derajatBebas + " Signifikansi: " + signifikansi);

		Tabelchi rowchi = statisticTables.getChiTabels().get(derajatBebas);
		double Xtabel = rowchi.get_Val(signifikansi);
		System.out.println("8. Nilai X: " + Xtabel);
		if (XHitung < Xtabel)
			System.out.println("H0 diTERIMA! berdistribusi normal.");
		else
			System.out.println("H0 diTOLAK! tdk berdistribusi normal.");

		System.out.println("________END CHI TEST_______");
		return XHitung < Xtabel;

	}

	public static Double[] getMEANSTDEV(Integer[] list, boolean sample) {
		int s = sample ? 1 : 0;
		Double[] meanstd = new Double[2];
		double totalData = 0.0;
		int N = list.length;
		for (int i = 0; i < N; i++) {
			totalData += list[i];
		}
		double rata = totalData / N, cumulative = 0.0;
		for (int i = 0; i < N; i++) {
			Double dif = Math.pow((list[i] - rata), 2);
			cumulative += dif;
		}
		Double stdev = Math.sqrt(cumulative / (N - s));
		meanstd[0] = rata;
		meanstd[1] = stdev;
		return meanstd;
	}

	private static int cariDataYangSama(List<itemTabelKumulatif> tabelKum, itemTabelKumulatif item) {
		int sama = 0;
		for (itemTabelKumulatif itk : tabelKum) {
			if (itk.getDataPoint() == item.getDataPoint())
				sama++;
		}
		return sama;
	}

	public boolean isNormalKolmogorov(Integer[] list_in, double signifikansi) {
		Integer[] list = urutkanList(list_in);
		Double[] meanstd = getMEANSTDEV(list, true);
		double stdev = meanstd[1], mean = meanstd[0];
		int N = list.length;
		NormalDistribution normS = new NormalDistribution(0, 1);
		System.out.println("________MULAI UJI KOLMOGOROV_______");
		System.out.println("1. mean: " + mean + " stdev: " + stdev + " N: " + N);
		List<itemTabelKumulatif> tabelKumulatif = new ArrayList<itemTabelKumulatif>();

		double kumulatifempiris = 0.0;
		for (int i = 0; i < list.length; i++) {
			itemTabelKumulatif itk = new itemTabelKumulatif(i, list[i]);
			itk.setMean(mean);
			itk.setStdev(stdev);
			itk.hitung();
			itk.setKumulatifNormal(normS.cumulativeProbability(itk.getZ()));
			tabelKumulatif.add(itk);
		}

		Double[] listDiffKumulatif = new Double[N];

		for (int i = 0; i < tabelKumulatif.size(); i++) {
			itemTabelKumulatif itk = tabelKumulatif.get(i);
			int ds = cariDataYangSama(tabelKumulatif, itk);
			itk.setDataSama(ds);
			if (i > 0) {
				if (tabelKumulatif.get(i - 1).getDataPoint() != itk.getDataPoint())
					kumulatifempiris += (double) ds / N;
			} else
				kumulatifempiris += (double) ds / N;
			Double diff = Math.abs(itk.getKumulatifNormal() - kumulatifempiris);
			listDiffKumulatif[i] = diff;
			itk.setKumulatifEmpiris(kumulatifempiris);
			itk.setDiffNormEmp(diff);
		}

		Double maxKumulatif = maxDouble(listDiffKumulatif);

		for (int i = 0; i < tabelKumulatif.size(); i++) {
			System.out.println(tabelKumulatif.get(i));
		}

		Tabelkolmogorov tk = statisticTables.getKolmogorovTables().get(N);
		if (tk == null)
			return false;
		double nilaiKolmogorov = tk.get_Val(signifikansi);

		System.out.println("MAx kumulatif: " + maxKumulatif + " nilai TABEL KOLMOGOROV(n=" + N + "sig=" + signifikansi
				+ "): " + nilaiKolmogorov);

		if (maxKumulatif < nilaiKolmogorov)
			System.out.println("H0 DITERIMA! DATA NORMAL");
		else
			System.out.println("H0 DITOLAK! DATA TDK NORMAL");

		System.out.println("________END KOLMOGOROV_______");
		return maxKumulatif < nilaiKolmogorov;

	}

	public static Date getDate(String s) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date d;
		try {
			d = formatter.parse(s);
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			// d.setDate(d.getDay()+1);
			Long l = d.getTime();

			return new java.sql.Date(l);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Date();
		}

	}

	public static String perindahNominal(Integer Int) {
		String nominal = Int.toString();
		String hasil = "";
		if (nominal.length() > 3) {
			int nol = 0;
			for (int i = nominal.length() - 1; i > 0; i--) {
				nol++;
				hasil = nominal.charAt(i) + hasil;
				if (nol == 3) {
					hasil = "." + hasil;
					nol = 0;
				}

			}
			hasil = nominal.charAt(0) + hasil;
		} else {
			hasil = Int.toString();
		}
		return hasil;
	}

	public static String perindahTanggal(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		Integer bulan = cal.get(Calendar.MONTH) + 1;
		return cal.get(Calendar.DATE) + "-" + bulan.toString() + "-" + cal.get(Calendar.YEAR);

	}

	public static String newCode(String namaPkm) {
		namaPkm = namaPkm.toUpperCase();
		String code = "";
		int gap = 3;
		int charIdx = 0;
		int length = (namaPkm.length()) * gap;
		for (int i = 0; i < length; i++) {
			int charChoice = random.nextInt(3) + 1;

			if (i % gap == 0) {
				char four = namaPkm.charAt(i / gap);
				code += four;
			} else {
				switch (charChoice) {
				case 1:
					charIdx = random.nextInt(charsLow.length);
					code += charsLow[charIdx];
					break;
				case 2:
					charIdx = random.nextInt(charsHi.length);
					code += charsHi[charIdx];
					break;
				case 3:
					charIdx = random.nextInt(charNum.length);
					code += charNum[charIdx];
					break;

				default:
					break;
				}
			}
		}

		return code;
	}

}
