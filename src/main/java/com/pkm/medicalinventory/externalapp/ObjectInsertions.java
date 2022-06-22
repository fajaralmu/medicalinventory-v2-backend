package com.pkm.medicalinventory.externalapp;

import java.util.ArrayList;
import java.util.List;

import com.pkm.medicalinventory.entity.HealthCenter;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.entity.Supplier;
import com.pkm.medicalinventory.entity.Unit;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class ObjectInsertions {

	static final String[] unitNames = new String[] { "Ampul", "Botol", "Box", "Kaplet", "kapsul", "Lembar", "Pot",
			"Sachet", "Tablet", "Vial", };
	static final String[][] supplierProps = new String[][] { { "System", "Default", "029283" },
			{ "PT Distribusi", "Semarang", "dist@email.com" }, { "PT Merica", "Pasar", "mer@email.com" },
			{ "PT ABC", "Temanggung", "1234" }, { "PT CDF", "Bandung", "02938" }, { "PT GHI", "Situbondo", "09912" },
			{ "PT XYZ", "Malang", "129437" },
			{ "Dinas Kesehatan", "Dinas Kesehatan Kabupaten Kebumen", "0287-18273-1293" },
			{ "PT MNO", "Cilacap", "9099897876" }, { "CV Testing", "selamat datang di malang", "9173-0282-1927" },
			{ "PT SEMBILAN", "Somewhere in the planet", "9273-283-283" },

	};
	static final String[][] hcProps = new String[][] {

			{ "Puskesmas Sehat", "Kecamatan Sruweng" }, { "Pustu Trikarso", "Desa Trikarso" },
			{ "Pustu Karangpule", "Karangpule" }, { "Pustu Pengempon", "Desa Pengempon" },
			{ "Pustu Kejawang", "Desa Kejawang" }, { "PKD Menganti", "Desa Menganti" },
			{ "PKD Giwangretno", "Desa Giwangretno" }, { "PKD Sruweng", "Kecamatan Sruweng" },
			{ "PKD Purwodeso", "Desa Purwodeso" }, { "PKD Jabres", "Desa Jabres" }, { "PKD Karangpule", "Karangpule" },
			{ "PKD Pakuran", "Desa Pakuran" }, { "PKD Pengempon", "Desa Pengempon" },
			{ "PKD Karangsari", "Desa Karangsari" }, { "PKD Tanggeran", "Desa Tanggeran" },
			{ "PKD Karangjambu", "Desa Karangjambu" }, { "PKD Pandansari", "Desa Pandansari" },
			{ "PKD Klepusanggar", "Desa Klepusanggar" }, { "PKD Sidoagung", "Desa Sidoagung" },
			{ "PKD Penusupan", "Desa Penusupan" }, { "PKD Donosari", "Desa Donosari" },
			{ "PKD Condong 1", "Desa Condong 2" }, { "PKD Condong 2", "Desa Condong" },

	};
	static Session session;

	public static void main(String[] args) {
		session = HibernateSessions.setSession();
//		addObj1();
		addObj2();
	}

	private static void addObj2() {
		List<Product> products = MedsList.getProducts();
		Transaction tx = session.beginTransaction();
		try {
		for (Product product : products) {
			session.save(product);
		}	tx.commit();
		} catch (Exception e) {
			if (null != tx) {
				tx.rollback();
			}
		} finally {
			session.close();
		}
		System.exit(0);
	}

	private static void addObj1() {
		List<Unit> units = generateUnits();
		List<Supplier> suppliers = generateSuppliers();
		List<HealthCenter> healthCenters = generateHealthCenters();
		List all = new ArrayList<>();
		all.addAll(units);
		all.addAll(suppliers);
		all.addAll(healthCenters);
		Transaction tx = session.beginTransaction();
		try {

			for (Object object : all) {
				session.save(object);
			}
			tx.commit();
		} catch (Exception e) {
			if (null != tx) {
				tx.rollback();
			}
		} finally {
			session.close();
		}
		System.exit(0);
	}

	private static List<HealthCenter> generateHealthCenters() {
		List<HealthCenter> list = new ArrayList<>();
		for (int i = 0; i < hcProps.length; i++) {
			String[] prop = hcProps[i];
			list.add(HealthCenter.builder().code("PKM" + i).name(prop[0]).address(prop[1]).build());
		}
		return list;
	}

	private static List<Supplier> generateSuppliers() {

		List<Supplier> suppliers = new ArrayList<>();
		for (int i = 0; i < supplierProps.length; i++) {
			String[] prop = supplierProps[i];
			suppliers.add(
					Supplier.builder().code("SUPPLIER" + i).name(prop[0]).address(prop[1]).contact(prop[2]).build());
		}
		return suppliers;
	}

	private static List<Unit> generateUnits() {
		List<Unit> units = new ArrayList<Unit>();
		for (int i = 0; i < unitNames.length; i++) {
			units.add(Unit.builder().name(unitNames[i]).description("DESC " + unitNames[i]).build());
		}

		return units;
	}
}
