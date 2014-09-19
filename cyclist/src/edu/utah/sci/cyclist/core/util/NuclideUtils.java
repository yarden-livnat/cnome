package edu.utah.sci.cyclist.core.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.utah.sci.cyclist.neup.model.IndeterminateNuclideForm;
import edu.utah.sci.cyclist.neup.model.NotNuclide;
import edu.utah.sci.cyclist.neup.model.Nuclide;

public class NuclideUtils {

	private NuclideUtils() {
	}

	/*
	 * Static functions
	 */

	static public int znum(int nuc) throws NotNuclide, IndeterminateNuclideForm {
		return id(nuc) / 10000000;
	}

	static public int znum(String name) throws NotNuclide,
			IndeterminateNuclideForm {
		return id(name) / 10000000;
	}

	static public int anum(int nuc) throws NotNuclide, IndeterminateNuclideForm {
		return (id(nuc) / 10000) % 1000;
	}

	static public int snum(int nuc) throws NotNuclide, IndeterminateNuclideForm {
		return id(nuc) % 10000;
	}

	static public int zzaaam(int nuc) throws NotNuclide,
			IndeterminateNuclideForm {
		int nucid = id(nuc);
		int zzzaaa = nucid / 10000;
		int ssss = nucid % 10000;
		if (10 <= ssss)
			ssss = 9;
		return zzzaaa * 10 + ssss;
	}

	static public int zzaaam_to_id(int nuc) {
		return (nuc / 10) * 10000 + (nuc % 10);
	};

	static public int zzzaaa(int nuc) throws NotNuclide,
			IndeterminateNuclideForm {
		return id(nuc) / 10000;
	}

	static public int zzzaaa_to_id(int nuc) {
		return nuc * 10000;
	}

	static public int zzllaaam_to_id(String code) throws NotNuclide {
		if (code.isEmpty())
			throw new NotNuclide(code, "<Empty>");

		int nucid = 0;
		String elem_name;

		// Get the string into a regular form
		String nucstr = code.toUpperCase();
		// Removing first two characters (redundant), for 1 digit nuclides, such
		// as 2-He-4, the first slash will be removed, and the second attempt to
		// remove the second slash will do nothing.
		// nucstr = nucstr.substring(2);
		// nucstr = pyne::remove_substring(nucstr, "-");
		// // Does nothing if nuclide is short, otherwise removes the second "-"
		// instance
		// nucstr = pyne::remove_substring(nucstr, "-");
		nucstr = nucstr.replace("-", "");

		int nuclen = nucstr.length();

		// Nuclide is probably in name form, or some variation therein
		// String anum_str = pyne::remove_characters(nucstr, pyne::alphabet);
		String anum_str = nucstr.replaceAll("[a-zA-Z]", "");

		// natural element form, a la 'U' -> 920000000
		if (anum_str.isEmpty() || nucstr.contains("NAT")) {
			elem_name = capitalize(nucstr.replace("NAT", ""));
			if (_name_zz.containsKey(elem_name))
				return 10000000 * _name_zz.get(elem_name);
		}
		int anum = Integer.valueOf(anum_str);

		// Figure out if we are meta-stable or not
		String end_char = nucstr.substring(nucstr.length() - 1);
		if (end_char == "M")
			nucid = (10000 * anum) + 1;
		else if (DIGITS.contains(end_char))
			nucid = (10000 * anum);
		else
			throw new NotNuclide(nucstr, nucid);

		// Add the Z-number
		// elem_name = pyne::remove_characters(nucstr.substr(0, nuclen-1),
		// pyne::digits);
		elem_name = nucstr.substring(0, nucstr.length() - 1).replaceAll(
				"[0-9]", "");
		elem_name = capitalize(elem_name);
		if (_name_zz.containsKey(elem_name))
			nucid = (10000000 * _name_zz.get(elem_name)) + nucid;
		else
			throw new NotNuclide(nucstr, nucid);
		return nucid;
	}

	static String capitalize(String str) {
		if (str.isEmpty())
			return str;
		return str.substring(0, 1).toUpperCase()
				+ str.substring(1).toLowerCase();
	}

	static public boolean isNuclide(int nuc) {
		int n;
		try {
			n = id(nuc);
		} catch (NotNuclide e) {
			return false;
		} catch (IndeterminateNuclideForm e) {
			return false;
		}

		if (n <= 10000000)
			return false;

		int zzz = n / 10000000;
		int aaa = (n % 10000000) / 10000;
		if (aaa == 0)
			return false; // is element

		if (aaa < zzz)
			return false;

		return true;
	}

	public static int id(int nuc) throws NotNuclide, IndeterminateNuclideForm {
		if (nuc < 0)
			throw new NotNuclide(nuc, "");

		int zzz = nuc / 10000000; // ZZZ ?
		int aaassss = nuc % 10000000; // AAA-SSSS ?
		int aaa = aaassss / 10000; // AAA ?
		// Nuclide must already be in id form
		if (0 < zzz && zzz <= aaa && aaa <= zzz * 7) {
			// Normal nuclide
			return nuc;
		} else if (aaassss == 0 && _zz_name.containsKey(zzz)) {
			// Natural elemental nuclide: ie for Uranium = 920000000
			return nuc;
		} else if (nuc < 1000 && _zz_name.containsKey(nuc))
			// Gave Z-number
			return nuc * 10000000;

		// Not in id form, try ZZZAAAM form.
		zzz = nuc / 10000; // ZZZ ?
		aaassss = nuc % 10000; // AAA-SSSS ?
		aaa = aaassss / 10; // AAA ?
		if (zzz <= aaa && aaa <= zzz * 7) {
			// ZZZAAAM nuclide
			return (zzz * 10000000) + (aaa * 10000) + (nuc % 10);
		} else if (aaa <= zzz && zzz <= aaa * 7 && _zz_name.containsKey(aaa)) {
			// Cinder-form (aaazzzm), ie 2350920
			return (aaa * 10000000) + (zzz * 10000) + (nuc % 10);
		}
		// else if (aaassss == 0 && 0 == _zz_name.containsKey(nuc/1000) && 0 <
		// _zz_name.containsKey(zzz))
		else if (aaassss == 0 && _zz_name.containsKey(zzz)) {
			// zzaaam form natural nuclide
			return zzz * 10000000;
		}

		if (nuc >= 1000000) {
			// From now we assume no metastable info has been given.
			throw new IndeterminateNuclideForm(nuc, "");
		}
		;

		// Nuclide is not in zzaaam form,
		// Try MCNP form, ie zzaaa
		// This is the same form as SZA for the 0th state.
		zzz = nuc / 1000;
		aaa = nuc % 1000;
		if (zzz <= aaa) {
			if (aaa - 400 < 0) {
				if (nuc == 95242)
					return nuc * 10000 + 1; // special case MCNP Am-242m
				else
					return nuc * 10000; // Nuclide in normal MCNP form
			} else {
				// Nuclide in MCNP metastable form
				if (nuc == 95642)
					return (95642 - 400) * 10000; // special case MCNP Am-242
				nuc = ((nuc - 400) * 10000) + 1;
				while (3.0 < (float) ((nuc / 10000) % 1000)
						/ (float) (nuc / 10000000))
					nuc -= 999999;
				return nuc;
			}
		} else if (aaa == 0 && _zz_name.containsKey(zzz)) {
			// MCNP form natural nuclide
			return zzz * 10000000;
		} else if (zzz > 1000) {
			// SZA form with a metastable state (sss != 0)
			int sss = zzz / 1000;
			int newzzz = zzz % 1000;

			return newzzz * 10000000 + aaa * 10000 + sss;
		}

		// Not a normal nuclide, might be a
		// Natural elemental nuclide.
		// ie 92 for Uranium = 920000
		if (_zz_name.containsKey(nuc))
			return nuc * 10000000;
		throw new IndeterminateNuclideForm(nuc, "");
	};

	static public int id(String nuc) throws NotNuclide,
			IndeterminateNuclideForm {
		if (nuc.isEmpty())
			throw new NotNuclide(nuc, "<empty>");
		int newnuc = 0;
		String lem_name;

		if (nuc.length() >= 5) { // nuc must be at least 4 characters or greater
									// if it is in ZZLLAAAM form.
			if (nuc.substring(1, 1 + 3).contains("-")
					&& nuc.substring(4, 4 + 5).contains("-")) {
				// Nuclide most likely in ZZLLAAAM Form, only form that contains
				// two "-"'s.
				int dashIndex = nuc.indexOf("-");
				String zz = nuc.substring(0, dashIndex);
				String ll_aaa_m = nuc.substring(dashIndex + 1);
				int dash2Index = ll_aaa_m.indexOf("-");
				String ll = ll_aaa_m.substring(0, dash2Index);
				int zz_int = Integer.valueOf(zz);
				if (znum(ll) == zz_int) { // Verifying that the LL and ZZ point
											// to the same element as secondary
					// verification that nuc is in ZZLLAAAM form.
					return zzllaaam_to_id(nuc);
				}
			}

			// Get the string into a regular form
			String nucstr = nuc.toUpperCase();
			nucstr = nucstr.replace("-", "");
			int nuclen = nucstr.length();
			String elem_name;

			if (DIGITS.contains(nucstr.substring(0, 1))) {
				if (DIGITS.contains(nucstr.substring(nuclen - 1))) {
					// Nuclide must actually be an integer that
					// just happens to be living in string form.
					newnuc = Integer.valueOf(nucstr);
					newnuc = id(newnuc);
				} else {
					// probably in NIST-like form (242Am)
					// Here we know we have both digits and letters
					String anum_str = nucstr.replaceAll("[a-zA-Z]", "");
					newnuc = Integer.valueOf(anum_str) * 10000;

					// Add the Z-number
					elem_name = nucstr.replaceAll("[0-9]", "");
					elem_name = capitalize(elem_name);
					if (_name_zz.containsKey(elem_name))
						newnuc = (10000000 * _name_zz.get(elem_name)) + newnuc;
					else
						throw new NotNuclide(nucstr, newnuc);
				}
			} else if (DIGITS.contains(nucstr.substring(0, 1))) {

				// Nuclide is probably in name form, or some variation therein
				String anum_str = nucstr.replaceAll("[a-zA-Z]", "");

				// natural element form, a la 'U' -> 920000000
				if (anum_str.isEmpty()) {
					elem_name = capitalize(nucstr);
					if (_name_zz.containsKey(elem_name))
						return 10000000 * _name_zz.get(elem_name);
				}

				int anum = Integer.valueOf(anum_str);

				// bad form
				if (anum < 0)
					throw new NotNuclide(nucstr, anum);

				// Figure out if we are meta-stable or not
				String end_char = nucstr.substring(nucstr.length() - 1);
				if (end_char == "M")
					newnuc = (10000 * anum) + 1;
				else if (DIGITS.contains(end_char))
					newnuc = (10000 * anum);
				else
					throw new NotNuclide(nucstr, newnuc);

				// Add the Z-number
				elem_name = nucstr.substring(0, nuclen - 1).replaceAll("[0-9]",
						"");
				elem_name = capitalize(elem_name);
				if (_name_zz.containsKey(elem_name))
					newnuc = (10000000 * _name_zz.get(elem_name)) + newnuc;
				else
					throw new NotNuclide(nucstr, newnuc);
			} else {
				// Clearly not a nuclide
				throw new NotNuclide(nuc, nucstr);
			}
		}
		return newnuc;
	}

	public static String name(int nuc) throws NotNuclide,
			IndeterminateNuclideForm {
		int nucid = id(nuc);
		String newnuc = "";

		int zzz = nucid / 10000000;
		int ssss = nucid % 10000;
		int aaassss = nucid % 10000000;
		int aaa = aaassss / 10000;

		// Make sure the LL value is correct
		if (!_zz_name.containsKey(zzz))
			throw new NotNuclide(nuc, nucid);

		// Add LL
		newnuc += _zz_name.get(zzz);

		// Add A-number
		if (0 < aaa)
			newnuc += String.valueOf(aaa);

		// Add meta-stable flag
		if (0 < ssss)
			newnuc += "M";

		return newnuc;
	};

	static private Set<Integer> name_to_zz_group(List<String> names) {
		Set<Integer> set = new HashSet<>();
		for (String name : names) {
			set.add(_name_zz.get(name));
		}
		return set;
	}

	/*
	 * static constants
	 */

	static private String DIGITS = "0123456789";
	static private String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	static private Map<String, Integer> _name_zz;
	static private Map<Integer, String> _zz_name;
	static private Map<String, Integer> _fluka_zz;
	static private Map<Integer, String> _zz_fluka;

	// Lanthanides
	static public List<String> LAN_IDS = Arrays.asList("La", "Ce", "Pr", "Nd",
			"Pm", "Sm", "Eu", "Gd", "Tb", "Dy", "Ho", "Er", "Tm", "Yb", "Lu");
	static public Set<String> LAN = new HashSet<>(LAN_IDS);
	static public Set<Integer> lan;

	// Actinides
	static public List<String> ACT_IDS = Arrays.asList("Ac", "Th", "Pa", "U",
			"Np", "Pu", "Am", "Cm", "Bk", "Cf", "Es", "Fm", "Md", "No", "Lr");
	static public Set<String> ACT = new HashSet<>(ACT_IDS);
	static public Set<Integer> act;

	// Transuarnics
	static public List<String> TRU_IDS = Arrays.asList("Np", "Pu", "Am", "Cm",
			"Bk", "Cf", "Es", "Fm", "Md", "No", "Lr", "Rf", "Db", "Sg", "Bh",
			"Hs", "Mt", "Ds", "Rg", "Cn", "Fl", "Lv");
	static public Set<String> TRU = new HashSet<>(TRU_IDS);
	static public Set<Integer> tru;

	// Minor Actinides
	static public List<String> MA_IDS = Arrays.asList("Np", "Am", "Cm", "Bk",
			"Cf", "Es", "Fm", "Md", "No", "Lr");
	static public Set<String> MA = new HashSet<String>(MA_IDS);
	static public Set<Integer> ma;

	// Fission Products
	static public List<String> FP_IDS = Arrays.asList("Ag", "Al", "Ar", "As",
			"At", "Au", "B", "Ba", "Be", "Bi", "Br", "C", "Ca", "Cd", "Ce",
			"Cl", "Co", "Cr", "Cs", "Cu", "Dy", "Er", "Eu", "F", "Fe", "Fr",
			"Ga", "Gd", "Ge", "H", "He", "Hf", "Hg", "Ho", "I", "In", "Ir",
			"K", "Kr", "La", "Li", "Lu", "Mg", "Mn", "Mo", "N", "Na", "Nb",
			"Nd", "Ne", "Ni", "O", "Os", "P", "Pb", "Pd", "Pm", "Po", "Pr",
			"Pt", "Ra", "Rb", "Re", "Rh", "Rn", "Ru", "S", "Sb", "Sc", "Se",
			"Si", "Sm", "Sn", "Sr", "Ta", "Tb", "Tc", "Te", "Ti", "Tl", "Tm",
			"V", "W", "Xe", "Y", "Yb", "Zn", "Zr");

	static public Set<String> FP = new HashSet<>(FP_IDS);
	static public Set<Integer> fp;

	static {
		_name_zz = new HashMap<String, Integer>() {
			{
				put("Be", 04);
				put("Ba", 56);
				put("Bh", 107);
				put("Bi", 83);
				put("Bk", 97);
				put("Br", 35);
				put("Ru", 44);
				put("Re", 75);
				put("Rf", 104);
				put("Rg", 111);
				put("Ra", 88);
				put("Rb", 37);
				put("Rn", 86);
				put("Rh", 45);
				put("Tm", 69);
				put("H", 01);
				put("P", 15);
				put("Ge", 32);
				put("Gd", 64);
				put("Ga", 31);
				put("Os", 76);
				put("Hs", 108);
				put("Zn", 30);
				put("Ho", 67);
				put("Hf", 72);
				put("Hg", 80);
				put("He", 02);
				put("Pr", 59);
				put("Pt", 78);
				put("Pu", 94);
				put("Pb", 82);
				put("Pa", 91);
				put("Pd", 46);
				put("Po", 84);
				put("Pm", 61);
				put("C", 6);
				put("K", 19);
				put("O", 8);
				put("S", 16);
				put("W", 74);
				put("Eu", 63);
				put("Es", 99);
				put("Er", 68);
				put("Md", 101);
				put("Mg", 12);
				put("Mo", 42);
				put("Mn", 25);
				put("Mt", 109);
				put("U", 92);
				put("Fr", 87);
				put("Fe", 26);
				put("Fm", 100);
				put("Ni", 28);
				put("No", 102);
				put("Na", 11);
				put("Nb", 41);
				put("Nd", 60);
				put("Ne", 10);
				put("Zr", 40);
				put("Np", 93);
				put("B", 05);
				put("Co", 27);
				put("Cm", 96);
				put("F", 9);
				put("Ca", 20);
				put("Cf", 98);
				put("Ce", 58);
				put("Cd", 48);
				put("V", 23);
				put("Cs", 55);
				put("Cr", 24);
				put("Cu", 29);
				put("Sr", 38);
				put("Kr", 36);
				put("Si", 14);
				put("Sn", 50);
				put("Sm", 62);
				put("Sc", 21);
				put("Sb", 51);
				put("Sg", 106);
				put("Se", 34);
				put("Yb", 70);
				put("Db", 105);
				put("Dy", 66);
				put("Ds", 110);
				put("La", 57);
				put("Cl", 17);
				put("Li", 03);
				put("Tl", 81);
				put("Lu", 71);
				put("Lr", 103);
				put("Th", 90);
				put("Ti", 22);
				put("Te", 52);
				put("Tb", 65);
				put("Tc", 43);
				put("Ta", 73);
				put("Ac", 89);
				put("Ag", 47);
				put("I", 53);
				put("Ir", 77);
				put("Am", 95);
				put("Al", 13);
				put("As", 33);
				put("Ar", 18);
				put("Au", 79);
				put("At", 85);
				put("In", 49);
				put("Y", 39);
				put("N", 07);
				put("Xe", 54);
				put("Cn", 112);
				put("Fl", 114);
				put("Lv", 116);
			}
		};

		_zz_name = new HashMap<Integer, String>();
		for (Map.Entry<String, Integer> entry : _name_zz.entrySet()) {
			_zz_name.put(entry.getValue(), entry.getKey());
		}
		//
		_fluka_zz = new HashMap<String, Integer>() {
			{
				put("BERYLLIU", 40000000);
				put("BARIUM", 560000000);
				put("BOHRIUM", 1070000000); // No fluka
				put("BISMUTH", 830000000);
				put("BERKELIU", 970000000); // No fluka
				put("BROMINE", 350000000);
				put("RUTHENIU", 440000000); // No fluka
				put("RHENIUM", 750000000);
				put("RUTHERFO", 1040000000);
				put("ROENTGEN", 1110000000);
				put("RADIUM", 880000000); // No fluka
				put("RUBIDIUM", 370000000); // No fluka
				put("RADON", 860000000); // no fluka
				put("RHODIUM", 450000000); // no fluka
				put("THULIUM", 690000000); // no fluka
				put("HYDROGEN", 10000000);
				put("PHOSPHO", 150000000);
				put("GERMANIU", 320000000);
				put("GADOLINI", 640000000);
				put("GALLIUM", 310000000);
				put("OSMIUM", 760000000); // no fluka
				put("HASSIUM", 1080000000);
				put("ZINC", 300000000);
				put("HOLMIUM", 670000000); // no fluka
				put("HAFNIUM", 720000000);
				put("MERCURY", 800000000);
				put("HELIUM", 20000000);
				put("PRASEODY", 590000000); // no fluka
				put("PLATINUM", 780000000);
				put("239-PU", 940000000); // "239-PU"
				put("LEAD", 820000000);
				put("PROTACTI", 910000000); // no fluka
				put("PALLADIU", 460000000); // no fluka
				put("POLONIUM", 840000000); // no fluka
				put("PROMETHI", 610000000); // no fluka
				put("CARBON", 60000000);
				put("POTASSIU", 190000000);
				put("OXYGEN", 80000000);
				put("SULFUR", 160000000);
				put("TUNGSTEN", 740000000);
				put("EUROPIUM", 630000000);
				put("EINSTEIN", 990000000); // no fluka
				put("ERBIUM", 680000000); // no fluka
				put("MENDELEV", 1010000000); // no fluka
				put("MAGNESIU", 120000000);
				put("MOLYBDEN", 420000000);
				put("MANGANES", 250000000);
				put("MEITNERI", 1090000000); // no fluka
				put("URANIUM", 920000000);
				put("FRANCIUM", 870000000); // no fluka
				put("IRON", 260000000);
				put("FERMIUM", 1000000000); // no fluka
				put("NICKEL", 280000000);
				put("NITROGEN", 70000000);
				put("NOBELIUM", 1020000000); // no fluka
				put("SODIUM", 110000000);
				put("NIOBIUM", 410000000);
				put("NEODYMIU", 600000000);
				put("NEON", 100000000);
				put("ZIRCONIU", 400000000);
				put("NEPTUNIU", 930000000); // no fluka
				put("BORON", 50000000);
				put("COBALT", 270000000);
				put("CURIUM", 960000000); // no fluka
				put("FLUORINE", 90000000);
				put("CALCIUM", 200000000);
				put("CALIFORN", 980000000); // no fluka
				put("CERIUM", 580000000);
				put("CADMIUM", 480000000);
				put("VANADIUM", 230000000);
				put("CESIUM", 550000000);
				put("CHROMIUM", 240000000);
				put("COPPER", 290000000);
				put("STRONTIU", 380000000);
				put("KRYPTON", 360000000);
				put("SILICON", 140000000);
				put("TIN", 500000000);
				put("SAMARIUM", 620000000);
				put("SCANDIUM", 210000000);
				put("ANTIMONY", 510000000);
				put("SEABORGI", 1060000000); // no fluka
				put("SELENIUM", 340000000); // no fluka
				put("YTTERBIU", 700000000); // no fluka
				put("DUBNIUM", 1050000000); // no fluka
				put("DYSPROSI", 660000000); // no fluka
				put("DARMSTAD", 1100000000); // no fluka
				put("LANTHANU", 570000000);
				put("CHLORINE", 170000000);
				put("LITHIUM", 030000000);
				put("THALLIUM", 810000000); // no fluka
				put("LUTETIUM", 710000000); // no fluka
				put("LAWRENCI", 1030000000); // no fluka
				put("THORIUM", 900000000); // no fluka
				put("TITANIUM", 220000000);
				put("TELLURIU", 520000000); // no fluka
				put("TERBIUM", 650000000);
				put("99-TC", 430000000); // "99-TC"
				put("TANTALUM", 730000000);
				put("ACTINIUM", 890000000); // no fluka
				put("SILVER", 470000000);
				put("IODINE", 530000000);
				put("IRIDIUM", 770000000);
				put("241-AM", 950000000); // "241-AM"
				put("ALUMINUM", 130000000);
				put("ARSENIC", 330000000);
				put("ARGON", 180000000);
				put("GOLD", 790000000);
				put("ASTATINE", 850000000); // no fluka
				put("INDIUM", 490000000);
				put("YTTRIUM", 390000000);
				put("XENON", 540000000);
				put("COPERNIC", 1120000000); // no fluka
				put("UNUNQUAD", 1140000000); // no fluka: UNUNQUADIUM,
												// "Flerovium"
				put("UNUNHEXI", 1160000000); // no fluka: UNUNHEXIUM ,
												// "Livermorium"
				put("HYDROG-1", 10010000);
				put("DEUTERIU", 10020000);
				put("TRITIUM", 10040000);
				put("HELIUM-3", 20030000);
				put("HELIUM-4", 20040000);
				put("LITHIU-6", 30060000);
				put("LITHIU-7", 30070000);
				put("BORON-10", 50100000);
				put("BORON-11", 50110000);
				put("90-SR", 380900000); // fluka "90-SR"
				put("129-I", 531290000); // fluka "129-I"
				put("124-XE", 541240000); // fluka "124-XE"
				put("126-XE", 541260000); // fluka "126-XE"
				put("128-XE", 541280000); // fluka "128-XE"
				put("130-XE", 541300000); // fluka "130-XE"
				put("131-XE", 541310000); // fluka "131-XE"
				put("132-XE", 541320000); // fluka "132-XE"
				put("134-XE", 541340000); // fluka "134-XE"
				put("135-XE", 541350000); // fluka "135-XE"
				put("136-XE", 541360000); // fluka "136-XE"
				put("135-CS", 551350000); // fluka "135-CS"
				put("137-CS", 551370000); // fluka "137-CS"
				put("230-TH", 902300000); // fluka "230-TH"
				put("232-TH", 902320000); // fluka "232-TH"
				put("233-U", 922330000); // fluka "233-U"
				put("234-U", 922340000); // fluka "234-U"
				put("235-U", 922350000); // fluka "235-U"
				put("238-U", 922380000); // fluka "238-U"
			}
		};

		_zz_fluka = new HashMap<Integer, String>();
		for (Map.Entry<String, Integer> entry : _fluka_zz.entrySet()) {
			_zz_fluka.put(entry.getValue(), entry.getKey());
		}

		lan = name_to_zz_group(LAN_IDS);
		act = name_to_zz_group(ACT_IDS);
		tru = name_to_zz_group(TRU_IDS);
		ma = name_to_zz_group(MA_IDS);
		fp = name_to_zz_group(FP_IDS);
	}
}
