package fr.ign.analyse.obj;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class Analyse {

	// general colelctions
	public Set<ProjetAnalyse> projetCollec = new HashSet<ProjetAnalyse>();
	public Set<ScenarAnalyse> scenarCollec = new HashSet<ScenarAnalyse>();
	public Set<ScenarAnalyseFile> fileCollec = new HashSet<ScenarAnalyseFile>();

	// project collections
	public List<String> cellMinCollec = new ArrayList<String>();
	public List<String> seuilCollec = new ArrayList<String>();
	public List<String> gridCollec = new ArrayList<String>();
	public List<String> dataCollec = new ArrayList<String>();

	// scenar collections
	public List<String> nMaxCollec = new ArrayList<String>();
	public List<String> strictCollec = new ArrayList<String>();
	public List<String> yagCollec = new ArrayList<String>();
	public List<String> ahpCollec = new ArrayList<String>();
	public List<String> seedCollec = new ArrayList<String>();

	// files collection
	public List<String> echelleCollec = new ArrayList<String>();
	public List<String> meanCollec = new ArrayList<String>();

	// gen attrib
	public String nameExplo = null;

	// machine readable constructor
	public Analyse(File files, String name, boolean machineReadable) {
		Pattern dbTiret = Pattern.compile("--");
		Pattern tiret = Pattern.compile("-");

		for (File scenar : files.listFiles()) {
			if (scenar.getName().startsWith(name)) {
				String[] decompNameProj = dbTiret.split(scenar.getName());
				makeProjCollection(decompNameProj[0]);
				String[] decompNameScProj = tiret.split(decompNameProj[1]);
				makeScenCollection(decompNameScProj[0]);
				makeFileCollection(decompNameScProj[1], decompNameScProj[2]);
			}
		}
		for (File file : files.listFiles()) {
			String[] decompName = dbTiret.split(file.getName());
			String[] decompNameProj = tiret.split(decompName[0]);
			// nom de l'explo
			// String nameExplo = decompNameProj[0];
			// Set les différents jeux de données
			String data = decompNameProj[1];
			// Set les différentes tailles minimales de cellules
			String size = decompNameProj[2].replace(".0", "").replace("CM", "");
			// Set les differents seuils et grilles
			String seuil = null;
			String grid = null;
			if (decompNameProj[3].startsWith("S0")) {
				seuil = decompNameProj[3].replace("S", "");
				grid = decompNameProj[4].replace("GP_", "");
			} else {
				seuil = decompNameProj[3].replace("S", "") + "-" + decompNameProj[4];
				grid = decompNameProj[5].replace("GP_", "");
			}
			String[] decompScenarEval = tiret.split(decompName[1]);
			Pattern underscore = Pattern.compile("_");
			String[] decompScenar = underscore.split(decompScenarEval[0]);
			String nMax = decompScenar[0];
			String strict = decompScenar[1];
			String yag = decompScenar[2];
			String ahp = decompScenar[3];
			String seed = decompScenar[5];
			//
			String meaning = decompScenarEval[1];
			String echelle = decompScenarEval[2].replace(".0.tif", "");
			ProjetAnalyse proj = new ProjetAnalyse(true, file, size, grid, seuil, data);
			projetCollec.add(proj);
			ScenarAnalyse sC = new ScenarAnalyse(true, file, file, size, grid, seuil, data, nMax, ahp, strict, yag, seed);
			scenarCollec.add(sC);
			ScenarAnalyseFile sCf = new ScenarAnalyseFile(true, file, file, size, grid, seuil, data, nMax, ahp, strict, yag, seed, echelle, meaning);
			fileCollec.add(sCf);
			System.out.println(sCf);
		}
	}

	/*
	 * private void DeleteDoubleProj() { for (int i = 0; i < projetCollec.size(); i++) { for (int j = i + 1; j < projetCollec.size(); j++) { if
	 * (projetCollec.get(i).equals(projetCollec.get(j))) { projetCollec.remove(j); j--; } } } }
	 * 
	 * private void DeleteDoubleScen() { for (int i = 0; i < scenarCollec.size(); i++) { for (int j = i + 1; j < scenarCollec.size(); j++) { if
	 * (scenarCollec.get(i).equals(scenarCollec.get(j))) { scenarCollec.remove(j); j--; } } } }
	 */

	// human readable - not re-tested
	public Analyse(File file, String name) {
		this(file.listFiles(), name);
	}

	public Analyse(File[] files, String name) {
		// get the different project parameters
		Pattern tiret = Pattern.compile("-");
		for (File folderProjet : files) {
			if (folderProjet.isDirectory() && folderProjet.getName().startsWith(name)) {
				String nameProj = folderProjet.getName();
				makeProjCollection(nameProj);
				// set scenar
				for (File scenarFile : folderProjet.listFiles()) {
					if (scenarFile.getName().startsWith("N")) {
						String[] decompNameScProj = tiret.split(scenarFile.getName());
						makeScenCollection(decompNameScProj[0]);
						makeFileCollection(decompNameScProj[1], decompNameScProj[2]);
					}
				}
			}
		}

		// la montagne russe
		for (File fileProjet : files) {
			for (String size : cellMinCollec) {
				for (String grid : gridCollec) {
					for (String seuil : seuilCollec) {
						for (String data : dataCollec) {
							if (fileProjet.getName().contains("CM" + size) && fileProjet.getName().contains("GP_" + grid) && fileProjet.getName().contains("S" + seuil)
									&& fileProjet.getName().contains(data)) {
								ProjetAnalyse proj = new ProjetAnalyse(false, fileProjet, size, grid, seuil, data);
								projetCollec.add(proj);
								for (File fileScenar : fileProjet.listFiles()) {
									for (String nMax : nMaxCollec) {
										for (String strict : strictCollec) {
											for (String yag : yagCollec) {
												for (String ahp : ahpCollec) {
													for (String seed : seedCollec) {
														if (fileScenar.getName().contains(nMax.toString()) && fileScenar.getName().contains(strict)
																&& fileScenar.getName().contains(yag) && fileScenar.getName().contains(ahp)
																&& fileScenar.getName().contains(seed.toString())) {
															ScenarAnalyse sC = new ScenarAnalyse(false, fileProjet, fileScenar, size, grid, seuil, data, nMax, ahp, strict, yag,
																	seed);
															scenarCollec.add(sC);
															for (String echelle : echelleCollec) {
																for (String mean : meanCollec) {
																	ScenarAnalyseFile sCf = new ScenarAnalyseFile(false, fileProjet, fileScenar, size, grid, seuil, data, nMax, ahp,
																			strict, yag, seed, echelle, mean);
																	fileCollec.add(sCf);
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public Analyse() {
	}

	public void makeProjCollection(String nameProj) {
		Pattern tiret = Pattern.compile("-");
		String[] decompNameProj = tiret.split(nameProj);
		// nom de l'explo
		nameExplo = decompNameProj[0];

		// Set les différentes tailles minimales de cellules
		String minCell = decompNameProj[2].replace(".0", "").replace("CM", "");
		if (!cellMinCollec.contains(minCell)) {
			cellMinCollec.add(minCell);
		}
		// Set les différents jeux de données
		String data = decompNameProj[1];
		if (!dataCollec.contains(data)) {
			dataCollec.add(data);
		}
		// Set les differents seuils et grilles
		String seui = null;
		String grille = null;
		if (decompNameProj[3].startsWith("S0")) {
			seui = decompNameProj[3].replace("S", "");
			grille = decompNameProj[4].replace("GP_", "");
		} else {
			seui = decompNameProj[3].replace("S", "") + "-" + decompNameProj[4];
			grille = decompNameProj[5].replace("GP_", "");
		}
		if (!seuilCollec.contains(seui)) {
			seuilCollec.add(seui);
		}
		if (!gridCollec.contains(grille)) {
			gridCollec.add(grille);
		}
	}

	public void makeScenCollection(String scenar) {
		Pattern underscore = Pattern.compile("_");
		String[] decompScenar = underscore.split(scenar);
		if (!nMaxCollec.contains(decompScenar[0])) {
			nMaxCollec.add(decompScenar[0]);
		}
		if (!strictCollec.contains(decompScenar[1])) {
			strictCollec.add(decompScenar[1]);
		}
		if (!yagCollec.contains(decompScenar[2])) {
			yagCollec.add(decompScenar[2]);
		}
		if (!ahpCollec.contains(decompScenar[3])) {
			ahpCollec.add(decompScenar[3]);
		}
		if (!seedCollec.contains(decompScenar[5])) {
			seedCollec.add(decompScenar[5]);
		}
	}

	public void makeFileCollection(String mean, String ech) {
		if (!meanCollec.contains(mean)) {
			meanCollec.add(mean);
		}
		String echelle = ech.replace(".0.tif", "");
		if (!echelleCollec.contains(echelle)) {
			echelleCollec.add(echelle);
		}
	}

	public List<String> getEchelleRange() {
		return echelleCollec;
	}

	/**
	 * retourne les échelles d'un projet dans l'ordre croissant, limité par un nombre "limit"
	 * 
	 * @param int
	 *            limit : le nombre limite d'échelles renvoyés
	 * @return ArrayList<String> une liste d'échelles
	 */
	public List<String> getEchelleRange(int limit) {
		List<Integer> echelleCollecLimitedtemp = new ArrayList<Integer>();
		for (int i = 0; i < echelleCollec.size(); i++) {
			echelleCollecLimitedtemp.add(Integer.valueOf(echelleCollec.get(i)));
		}
		Collections.sort(echelleCollecLimitedtemp);
		List<String> echelleCollecLimited = new ArrayList<String>();
		for (int i = 0; i < limit; i++) {
			echelleCollecLimited.add(String.valueOf(echelleCollecLimitedtemp.get(i)));
		}

		return echelleCollecLimited;
	}

	public int getNumberProject() {
		return projetCollec.size();
	}

	public Set<ProjetAnalyse> getProjectCollec() {
		return projetCollec;
	}

	public Set<ScenarAnalyse> getScenarCollec() {
		return scenarCollec;
	}

	public List<Set<ScenarAnalyse>> getProjetBySeuil() throws FileNotFoundException {
		List<Set<ProjetAnalyse>> listGen = new ArrayList<Set<ProjetAnalyse>>();
		for (String cellMin : cellMinCollec)
			for (String grid : gridCollec) {
				for (String data : dataCollec) {
					Set<ProjetAnalyse> particularList = new HashSet<ProjetAnalyse>();
					for (ProjetAnalyse pa : projetCollec) {
						if (pa.getSizeCell().equals(cellMin) && pa.getData().equals(data) && pa.getGrid().equals(grid)) {
							particularList.add(pa);
						}
					}
					listGen.add(particularList);
				}
			}
		return getScenars(listGen);
	}

	public List<Set<ScenarAnalyse>> getProjetByGrid() throws FileNotFoundException {
		List<Set<ProjetAnalyse>> listGen = new ArrayList<Set<ProjetAnalyse>>();
		for (String cellMin : cellMinCollec)
			for (String seuil : seuilCollec) {
				for (String data : dataCollec) {
					Set<ProjetAnalyse> particularList = new HashSet<ProjetAnalyse>();
					for (ProjetAnalyse pa : projetCollec) {
						if (pa.getSizeCell().equals(cellMin) && pa.getData().equals(data) && pa.getSeuil().equals(seuil)) {
							particularList.add(pa);
						}
					}
					listGen.add(particularList);
				}
			}
		return getScenars(listGen);
	}

	public List<Set<ProjetAnalyse>> getProjetByCellmin() {
		List<Set<ProjetAnalyse>> listGen = new ArrayList<Set<ProjetAnalyse>>();
		for (String seuil : seuilCollec) {
			for (String grid : gridCollec) {
				for (String data : dataCollec) {
					Set<ProjetAnalyse> particularList = new HashSet<ProjetAnalyse>();
					for (ProjetAnalyse pa : projetCollec) {
						if (pa.getSeuil().equals(seuil) && pa.getData().equals(data) && pa.getGrid().equals(grid)) {
							particularList.add(pa);
						}
					}
					listGen.add(particularList);
				}
			}
		}
		return listGen;
	}

	public List<List<ProjetAnalyse>> getProjetByData() {
		List<List<ProjetAnalyse>> listGen = new ArrayList<List<ProjetAnalyse>>();
		for (String seuil : seuilCollec) {
			for (String grid : gridCollec) {
				for (String cellMin : cellMinCollec) {
					List<ProjetAnalyse> particularList = new ArrayList<ProjetAnalyse>();
					for (ProjetAnalyse pa : projetCollec) {
						if (pa.getSeuil().equals(seuil) && pa.getSizeCell().equals(cellMin) && pa.getGrid().equals(grid)) {
							particularList.add(pa);
						}
					}
					listGen.add(particularList);
				}
			}
		}
		return listGen;
	}

	public List<List<ScenarAnalyse>> getScenarPerProject() {
		List<List<ScenarAnalyse>> result = new ArrayList<>();
		for (ProjetAnalyse proj : projetCollec) {
			List<ScenarAnalyse> scenars = new ArrayList<ScenarAnalyse>();
			for (ScenarAnalyse scenar : scenarCollec) {
				if (scenar.getProjFile().equals(proj.getProjFile())) {
					scenars.add(scenar);
				}
			}
			result.add(scenars);
		}
		return result;
	}

	public List<ScenarAnalyseFile> getFilesPerScenar(ScenarAnalyse sA) {
		List<ScenarAnalyseFile> result = new ArrayList<>();
		for (ScenarAnalyseFile sAf : fileCollec) {
			if (sAf.getScenarName().equals(sA.getScenarName())) {
				result.add(sAf);
			}
		}
		return result;
	}

	/**
	 * Méthode qui retourne les ScenarAnalyses classées selon les scénarios, pour tout types de projets
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	public List<Set<ScenarAnalyse>> getScenars() throws FileNotFoundException {
		List<Set<ProjetAnalyse>> uniList = new ArrayList<Set<ProjetAnalyse>>();
		uniList.add(projetCollec);
		return getScenars(uniList);
	}

	/**
	 * Méthode qui retourne les ScenarAnalyses classées selon les scénarios, pour une liste de projets donnée
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	public List<Set<ScenarAnalyse>> getScenars(List<Set<ProjetAnalyse>> listProjets) throws FileNotFoundException {

		List<Set<ScenarAnalyse>> listScenars = new ArrayList<Set<ScenarAnalyse>>();

		for (Set<ProjetAnalyse> listProjet : listProjets) {
			Hashtable<String, Set<ScenarAnalyse>> collec = new Hashtable<String, Set<ScenarAnalyse>>();
			for (ProjetAnalyse pA : listProjet) {
				for (ScenarAnalyse scenar : scenarCollec) {
					if (scenar.getProjFile().equals(pA.getProjFile())) {
						if (collec.containsKey(scenar.getScenarName())) {
							Set<ScenarAnalyse> listTemp = collec.get(scenar.getScenarName());
							listTemp.add(scenar);
							collec.put(scenar.getScenarName(), listTemp);
						} else {
							Set<ScenarAnalyse> resultTemp = new HashSet<ScenarAnalyse>();
							resultTemp.add(scenar);
							collec.put(scenar.getScenarName(), resultTemp);
						}
					}
				}
			}
			for (Set<ScenarAnalyse> lists : collec.values()) {
				listScenars.add(lists);
			}
		}
		return listScenars;
	}

	public List<List<ScenarAnalyse>> getScenarDiffSeed() throws FileNotFoundException {
		List<List<ScenarAnalyse>> scenPerProj = getScenarPerProject();
		List<List<ScenarAnalyse>> result = new ArrayList<>();
		for (List<ScenarAnalyse> scenProj : scenPerProj) {
			for (String yag : yagCollec) {
				for (String n : nMaxCollec) {
					for (String ahp : ahpCollec) {
						for (String str : strictCollec) {
							List<ScenarAnalyse> sortedList = new ArrayList<ScenarAnalyse>();
							for (ScenarAnalyse scen : scenProj) {
								if (scen.getAhp().equals(ahp) && scen.getnMax().equals(n) && scen.isStrict().equals(str) && scen.isYag().equals(yag)) {
									sortedList.add(scen);
								}
							}
							if (!sortedList.isEmpty()) {
								result.add(sortedList);
							}
						}
					}
				}
			}
		}

		return result;
	}

	public List<File> getSimuFiles(ScenarAnalyse sA) {
		List<File> lFinale = new ArrayList<>();

		for (File f : sA.getProjFile().getParentFile().listFiles()) {
			if (f.getName().startsWith(nameExplo)) {
				ProjetAnalyse sB = new ScenarAnalyse(f);
				if (((ScenarAnalyse) sB).equals(sA)) {
					if (sB.equals(sA)) {
						lFinale.add(f);
					}
				}
			}
		}
		return lFinale;
	}

	public File getSimuFile(ScenarAnalyse sA) throws FileNotFoundException {
		return getSimuFile(sA, "20", "evalAnal");
	}

	public File getSimuFile(ScenarAnalyse sA, String ech, String mean) throws FileNotFoundException {
		for (ScenarAnalyseFile sCf : fileCollec) {
			if (((ScenarAnalyse) sCf).sameScenario(sA)) {
				System.out.println(sCf.getFileFile());
				System.out.println(sCf.getEchelle());
				if (sCf.getEchelle().equals(ech)) {
					if (sCf.getMeaning().equals(mean)) {
						System.out.println("returned scale : " + sCf.getEchelle());
						System.out.println("returned file : " + sCf.getFileFile());
						return sCf.getFileFile();
					}
				}
			}
		}
		throw new FileNotFoundException();
	}

}
