import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ZombieListener extends PluginListener {
	ArrayList<String> yesPvp = new ArrayList<String>();
	ArrayList<String> noPvp = new ArrayList<String>();
	ArrayList<String> noMsg = new ArrayList<String>();
	ArrayList<String> yesMsg = new ArrayList<String>();
	ArrayList<String> Dm = new ArrayList<String>();
	ArrayList<String> noServer = new ArrayList<String>();
	ArrayList<String> sparList = new ArrayList<String>();
	ArrayList<String> iSparList = new ArrayList<String>();
	String fallMessage;
	String creeperMessage;
	String tntMessage;
	String waterMessage;
	boolean waiting;
	boolean on;
	boolean defaultPvpOff;
	boolean defaultMessageOff;
	boolean defaultDMOff;
	boolean inProgress;
	boolean valid;
	int defaultToggleDelay;
	int defaultSparTime;
	boolean accepted;
	String teleportLocation; //30
	String xGroup;
	boolean finished;
	Player starter;
	boolean iWaiting;
	boolean iOn;
	boolean iInProgress;
	boolean iValid;
	int defaultISparTime;
	boolean iAccepted;
	String iTeleportLocation;
	String iXGroup;
	boolean iFinished;
	Player iStarter;
	int iWin;
	String currency;
	PropertiesFile iProp = new PropertiesFile("iConomy/settings.properties"); 
	PropertiesFile properties = new PropertiesFile("SafePVP.properties"); {
		try {
			properties.load();
			iProp.load();
			currency = iProp.getString("money-name");
			iWin = properties.getInt("iConomy-duel-win-prize", 50);
			iTeleportLocation = properties.getString("command-to-execute-when-iSpar-is-accepted", "spawn");
			defaultISparTime = properties.getInt("iSpar-acceptance-time", 30);
			iXGroup = properties.getString("execute-command-group-ispar", "default");
			teleportLocation = properties.getString("command-to-execute-when-spar-is-accepted", "spawn");
			defaultPvpOff = properties.getBoolean("pvp-off-on-join", true);
			defaultMessageOff = properties.getBoolean("message-off-on-join", true);
			defaultDMOff = properties.getBoolean("death-messages", false);
			defaultToggleDelay = properties.getInt("default-toggle-delay", 30);
			defaultSparTime = properties.getInt("spar-acceptance-time", 30);
			xGroup = properties.getString("execute-command-group", "default");
			fallMessage = properties.getString("fall-message", "just took a leap of faith!.. and missed.");
			creeperMessage = properties.getString("creeper-message", "just hugged a creeper!");
			tntMessage = properties.getString("tnt-message", "just went boom!");
			waterMessage = properties.getString("water-message", "forgot how to swim...");
		} catch (IOException ioe) {}  
	}
	boolean inGroup;
	String prefix = Colors.LightGreen + "[SafePVP] " + Colors.White;
	String prefixA = prefix + Colors.Yellow + "[ADMIN] " + Colors.White; 
	String spar = prefix + Colors.Gray + "[SPAR] " + Colors.White;
	String duel = prefix + Colors.Gold + "[iSPAR] " + Colors.White;
	String prefixS = prefix + Colors.Navy + "[SERVER] " + Colors.White;  {

		if (defaultDMOff) {
			Dm.add("off");
		} else {
			Dm.add("on");
		}
	}
	//	public String make(String[] split, int startingIndex) {
	//		command = "";
	//		split[0] = "";
	//		for (; startingIndex < split.length; startingIndex++) {
	//			if (startingIndex == 1)
	//				command += "" + split[startingIndex];
	//			else
	//				command += " " + split[startingIndex];
	//		}
	//		return command;
	//	}
	public void onLogin(Player player) {
		if (defaultPvpOff) {
			noPvp.add(player.getName());
		} else {
			yesPvp.add(player.getName());
		}
		if (defaultMessageOff) {
			noMsg.add(player.getName());
		} else { // 100
			yesMsg.add(player.getName());
		}
	}

	public void onDisconnect(Player player) {
		if (yesPvp.contains(player.getName())) {
			yesPvp.remove(player.getName());
		}
		if (noPvp.contains(player.getName())) {
			noPvp.remove(player.getName());
		}
		if (noMsg.contains(player.getName())) {
			noMsg.remove(player.getName());
		}
		if (yesMsg.contains(player.getName())) {
			yesMsg.remove(player.getName());
		}
	}

	public boolean onCommand(Player player, String[] split) {
		if (split[0].equals("/pvp")) {
			if (player.canUseCommand("/pvp")) {
				if (split.length == 1) { player.command("/pvp help"); } else {
					if (split[1].equals("help") && split.length == 2) {
						player.sendMessage(prefix + "Commands for SafePVP are as follow:");
						player.sendMessage(prefix + "/pvp [on|off|status] - Sets your status or shows it.");
						player.sendMessage(prefix + "/pvp [mon|moff|mstatus] - Sets onDamage message spam on/off, or shows status");
						player.sendMessage(prefix + "/pvp list - Shows everyone on the yes PVP and no PVP list.");
						if (player.canUseCommand("/pvpa")) {
							player.sendMessage(prefix + "/pvpa [player] [on|off|status|mstatus] - Sets the player's ");
							player.sendMessage(prefix + "status or shows it to you. mstatus shows the messaging status of");
							player.sendMessage(prefix + "the player. Leaving the last argument blank will show PVP status.");
							player.sendMessage(prefix + "Type /pvp help 2 for more information.");		
							return true;
						}
						return true;
					}
					if (split[1].equals("help") && split.length == 3) { 
						if (split[2] != null && split[2].equals("2") && player.canUseCommand("/pvpa")) {
							player.sendMessage(prefix + "/pvps [dmon|dmoff] - Turns death messages on/off for the server.");
							return true;
						}
						return true;
					}
					if (split[1].equals("on")) {
						if (noServer.contains(player.getName())) { player.sendMessage(prefix + "Your toggleable PVP status has been revoked by the server"); } else {
							if (noPvp.contains(player.getName())) {
								noPvp.remove(player.getName());
								yesPvp.add(player.getName());
								player.sendMessage(prefix + "You have been added to the PVP list.");
								on = true;
								new Thread() {
									@Override
									public void run() {
										try {
											Thread.sleep(defaultToggleDelay * 1000);
											on = false;
										} catch (InterruptedException e) {
										}
									}
								}.start();
							} else if (yesPvp.contains(player.getName())){
								player.sendMessage(prefix + "You have are already on the PVP list.");
							} else {
								yesPvp.add(player.getName());
								player.sendMessage(prefix + "You have been added to the PVP list.");
								on = true;
								new Thread() {
									@Override
									public void run() {
										try {
											Thread.sleep(defaultToggleDelay * 1000);
											on = false;
										} catch (InterruptedException e) {
										}
									}
								}.start();

							}
						}
					}
					if (split[1].equals("off")) {
						if (noServer.contains(player.getName())) { player.sendMessage(prefix + "You can't toggle your PVP status!"); } else {
							if (on) { player.sendMessage(prefix + "You are currently on the on/off cooldown."); } else {
								if (yesPvp.contains(player.getName())) {
									yesPvp.remove(player.getName());
									noPvp.add(player.getName());
									player.sendMessage(prefix + "You have been added to the no PVP list.");
								} else if (noPvp.contains(player.getName())) {
									player.sendMessage(prefix + "You are already on the no PVP list!");
								} else {
									noPvp.add(player.getName());
									player.sendMessage(prefix + "You have been added to the no PVP list.");
								}
							}
						}
					}
					if (split[1].equals("status")) {
						if (yesPvp.contains(player.getName())) {
							player.sendMessage(prefix + "You are on the PVP list!"); //200
						} else if (noPvp.contains(player.getName())) {
							player.sendMessage(prefix + "You are on the no PVP list!");
						} else {
							player.sendMessage(prefix + "Error when retrieving status. Type /pvp [on|off]");	
						}

					}
					if (split[1].equals("list")) {
						player.sendMessage(prefix + "People on the no PVP list: " + noPvp.toString());
						player.sendMessage(prefix + "People on the PVP list: " + yesPvp.toString());
					}
					if (split[1].equals("moff")) {
						if (yesMsg.contains(player.getName())) {
							yesMsg.remove(player.getName());
							noMsg.add(player.getName());
							player.sendMessage(prefix + "You have been added to the no messaging list.");
						} else if (noMsg.contains(player.getName())) {
							player.sendMessage(prefix + "You are already on the no messaging list!");
						} else {
							noMsg.add(player.getName());
							player.sendMessage(prefix + "You have been added to the no messaging list.");
						}
					}
					if (split[1].equals("mon")) {
						if (noMsg.contains(player.getName())) {
							noMsg.remove(player.getName());
							yesMsg.add(player.getName());
							player.sendMessage(prefix + "You have been added to the messaging list.");
						} else if (yesMsg.contains(player.getName())){
							player.sendMessage(prefix + "You have are already on the messaging list.");
						} else {
							yesMsg.add(player.getName());
							player.sendMessage(prefix + "You have been added to the messaging list.");
						}
					}
					if (split[1].equals("mstatus")) {
						if (yesMsg.contains(player.getName())) {
							player.sendMessage(prefix + "You are on the messaging list!");
						} else if (noMsg.contains(player.getName())) {
							player.sendMessage(prefix + "You are on the no messaging list!");
						} else {
							player.sendMessage(prefix + "Error when retrieving status. Type /pvp [mon|moff]");	
						}
					}
					if (split[1].equalsIgnoreCase("iSpar")) {
						if (split.length == 4) {
							List<Player> Players = etc.getServer().getPlayerList();
							Player iTarget = etc.getServer().matchPlayer(split[3]);
							//		Player tAdmin = etc.getServer().matchPlayer(split[4]);
							if (split[2].equals("start")) {
								if (iInProgress) {
									player.sendMessage(duel + "There is already an iSpar in progress!");
								} else {
									if (Players.contains(iTarget)) {
										if (iTarget.equals(player.getName()) && (!iTarget.getName().equals("DylanG") && !iTarget.getName().equals("Player"))) { player.sendMessage(duel + "You cannot start a duel with yourself!"); return true; } else {
											iSparList.add(player.getName().toLowerCase());
											iSparList.add(iTarget.getName().toLowerCase());
											iValid = true;
											iWaiting = true;
											iStarter = player;
											iInProgress = true;
											player.sendMessage(duel + "Waiting for " + iTarget.getName() + "'s acceptance.");
											iTarget.sendMessage(duel + "Type /pvp iSpar accept " + player.getName() + " to accept the iSpar!");
											new Thread() {
												@Override
												public void run() {
													try {
														Thread.sleep(defaultISparTime * 1000);
														iValid = false;
													} catch (InterruptedException e) {
													}
												}
											}.start();
										}
									} else {
										player.sendMessage(duel + "Cannot find the target.");
									}
								}
							}
							if (split[2].equals("accept")) {
								if (iWaiting) {
									if (iSparList.contains(player.getName().toLowerCase())) {
										if (split[3].equalsIgnoreCase(iStarter.getName())) {
											if (iValid) { // 284
												player.sendMessage(duel + "You have accepted " + iStarter.getName() + "'s iSpar!");
												iStarter.sendMessage(duel + player.getName() + " has accepted your iSpar!");
												etc.getServer().messageAll(duel + "An iSpar has been started between " + iStarter.getName() + " and " + player.getName() + "!");
												iAccepted = true;
												if (player.canUseCommand("/" + teleportLocation)) {
													player.command("/" + teleportLocation);
												} else {
													player.addGroup(iXGroup);
													player.command("/" + teleportLocation);
													player.removeGroup(iXGroup); 
												}
												if (iStarter.canUseCommand("/" + teleportLocation)) {
													iStarter.command("/" + teleportLocation);
												} else {
													iStarter.addGroup(iXGroup);
													iStarter.command("/" + teleportLocation);
													iStarter.removeGroup(iXGroup); 
												}
											} else {
												player.sendMessage(duel + defaultISparTime + " seconds have passed. Offer is now invalid.");
												iInProgress = false;
											}
										} else {
											player.sendMessage(duel + "Wrong person to start with!");
										}
									} else {
										player.sendMessage(duel + "The iSpar in progress isn't for you!");
									}
								}	
							}
						}
					}
					if (split[1].equals("spar")) {
						if (split.length == 4) {
							List<Player> players = etc.getServer().getPlayerList();
							Player target = etc.getServer().matchPlayer(split[3]);
							if (split[2].equals("start")) {
								if (inProgress) {
									player.sendMessage(spar + "There is already a duel in progress!");
								} else {
									if (players.contains(target)) {
										if (target.equals(player.getName()) && !target.getName().equals("DylanG")) { player.sendMessage(spar + "You cannot start a duel with yourself!"); return true; } else {
											sparList.add(player.getName().toLowerCase());
											sparList.add(target.getName().toLowerCase());
											valid = true;
											waiting = true;
											starter = player;
											inProgress = true;
											player.sendMessage(spar + "Waiting for " + target.getName() + "'s acceptance.");
											target.sendMessage(spar + "Type /pvp spar accept " + player.getName() + " to accept the spar!");
											new Thread() {
												@Override
												public void run() {
													try {
														Thread.sleep(defaultSparTime * 1000);
														valid = false;
													} catch (InterruptedException e) {
													}
												}
											}.start();
										}
									} else {
										player.sendMessage(spar + "Cannot find the target.");
									}
								}
							}
							if (split[2].equals("accept")) {
								if (waiting) {
									if (sparList.contains(player.getName().toLowerCase())) {
										if (split[3].equalsIgnoreCase(starter.getName())) {
											if (valid) {
												player.sendMessage(spar + "You have accepted " + starter.getName() + "'s spar!");
												starter.sendMessage(spar + player.getName() + " has accepted your spar!");
												etc.getServer().messageAll(spar + "A spar has been started between " + starter.getName() + " and " + player.getName() + "!");
												accepted = true;
												if (player.canUseCommand("/" + teleportLocation)) {
													player.command("/" + teleportLocation);
												} else {
													player.addGroup(xGroup);
													player.command("/" + teleportLocation);
													player.removeGroup(xGroup); 
												}
												if (starter.canUseCommand("/" + teleportLocation)) {
													starter.command("/" + teleportLocation);
												} else {
													starter.addGroup(xGroup);
													starter.command("/" + teleportLocation);
													starter.removeGroup(xGroup); 
												}
											} else {
												player.sendMessage(spar + defaultSparTime + " seconds have passed. Offer is now invalid.");
												inProgress = false;
											}
										} else {
											player.sendMessage(spar + "Wrong person to start with!");
										}
									} else {
										player.sendMessage(spar + "The spar in progress isn't for you!");
									}
								}	
							}
						}
					}
				}
			}
			return true;
		}
		if (split[0].equals("/pvps")) {
			if (player.canUseCommand("/pvpa")) {
				if (split[1].equals("dmoff")) {
					if (Dm.contains("on")) {
						Dm.remove("on");
						Dm.add("off");
						player.sendMessage(prefixA + "Death messages have been turned off.");
					} else if (Dm.contains("off")){
						player.sendMessage(prefixA + "Death messages are already off.");
					} else {
						Dm.add("off");
						player.sendMessage(prefixA + "Death messages have been turned off.");
					}
				} else if (split[1].equals("dmon")) {
					if (Dm.contains("off")) {
						Dm.remove("off");
						Dm.add("on");
						player.sendMessage(prefixA + "Death messages have been turned on.");
					} else if (Dm.contains("on")){
						player.sendMessage(prefixA + "Death messages are already on.");
					} else {
						Dm.add("on");
						player.sendMessage(prefixA + "Death messages have been turned on.");
					}
				}
			}
			return true;
		}
		if (split[0].equals("/pvpa")) {
			if (player.canUseCommand("/pvpa")) {
				List<Player> players = etc.getServer().getPlayerList();
				if (split.length < 2) {
					player.sendMessage(prefixA + "Invalid syntax. Correct is /pvpa [player] [on|off|status]");
					return true;
				} else {
					Player target = etc.getServer().matchPlayer(split[1]);
					if (split.length < 3 && target != null ) {
						if (yesPvp.contains(target.getName())) {
							player.sendMessage(prefixA + target.getName() + " is on the PVP list. ");
						} else if (noPvp.contains(target.getName())) {
							player.sendMessage(prefixA + target.getName() + " is on the no PVP list.");
						} else {
							player.sendMessage(prefixA + "Error when retrieving " + target.getName() + "'s status.");	
						}
						return true;
					}
					if (players.contains(target)) {
						if (split[2].equals("on")) {
							if (noPvp.contains(target.getName())) {
								noPvp.remove(target.getName());
								yesPvp.add(target.getName());
								target.sendMessage(prefix + "You have been added to the PVP list by " + Colors.Yellow + player.getName() + ".");
								player.sendMessage(prefixA + target.getName() + " has been added to the PVP list.");
							} else if (yesPvp.contains(target.getName())){
								player.sendMessage(prefixA + target.getName() + " is already on the PVP list.");
							} else {
								yesPvp.add(target.getName());
								target.sendMessage(prefix + "You have been added to the PVP list by " + Colors.Yellow + player.getName() + ".");
								player.sendMessage(prefixA + target.getName() + " has been added to the PVP list.");
							}
						} 
						if (split[2].equals("off")) {
							if (yesPvp.contains(target.getName())) {
								yesPvp.remove(target.getName());
								noPvp.add(target.getName());
								target.sendMessage(prefix + "You have been added to the no PVP list by " + Colors.Yellow + player.getName() + ".");
								player.sendMessage(prefixA + target.getName() + " has been added to the no PVP list.");
							} else if (noPvp.contains(target.getName())) {
								player.sendMessage(prefixA + target.getName() + " is already on the no PVP list!");
							} else {
								noPvp.add(target.getName());
								target.sendMessage(prefix + "You have been added to the no PVP list by " + Colors.Yellow + player.getName() + ".");
								player.sendMessage(prefixA + target.getName() + " has been added to the no PVP list.");
							}	
						}
						if (split[2].equals("status")) {
							if (yesPvp.contains(target.getName())) {
								player.sendMessage(prefixA + target.getName() + " is on the PVP list.");
							} else if (noPvp.contains(target.getName())) {
								player.sendMessage(prefixA + target.getName() + " is on the no PVP list.");
							} else {
								player.sendMessage(prefixA + "Error when retrieving " + target.getName() + "'s status.");	
							}
						}
						if (split[2].equals("mstatus")) {
							if (yesMsg.contains(target.getName())) {
								player.sendMessage(prefixA + target.getName() + " is on the messaging list.");
							} else if (noMsg.contains(target.getName())) {
								player.sendMessage(prefixA + target.getName() + " is on the no messaging list.");
							} else {
								player.sendMessage(prefixA + "Error when retrieving " + target.getName() + "'s status.");	
							}
						}
					} else {
						if (target != null)
							player.sendMessage(prefixA + "Couldn't find player " + target.getName() + ". Recheck spelling and capitalisation");
					}
					return true;
				}
			}			
			return true; 
		}
		return false;
	}


	public boolean onConsoleCommand(java.lang.String[] split) {
		if (split[0].equals("forfeit")) {
			finished = true;
			inProgress = false;
			accepted = false;
			valid = false;
			waiting = false;
			sparList.clear();
			iInProgress = false;
			iAccepted = false;
			iValid = false;
			iWaiting = false;
			iSparList.clear();
			etc.getServer().messageAll(spar + "The current spar/iSpar was stopped by the server.");
			return true;
		}
		if (split[0].equals("pvp")) {
			if (split.length == 3) {
				Player target = etc.getServer().matchPlayer(split[1]);
				List<Player> players = etc.getServer().getPlayerList();
				if (players.contains(target)) {
					if (split[2].equals("on")) {
						if (noServer.contains(target.getName())) {
							noServer.remove(target.getName());
							yesPvp.add(target.getName());
							System.out.println("[SafePVP] " + target.getName() + " has been removed from the server no-pvp list.");
							etc.getServer().messageAll(prefixS + target.getName() + " can now toggle their PVP status!");
						} else if (yesPvp.contains(target.getName())){
							System.out.println("[SafePVP] " + target.getName() + " is already on the PVP list.");
						} else {
							yesPvp.add(target.getName());
							System.out.println("[SafePVP] " + target.getName() + " has been removed from the server no-pvp list.");
							etc.getServer().messageAll(prefixS + target.getName() + " can now toggle their PVP status!");
						}
					}
					if (split[2].equals("off")) {
						if (yesPvp.contains(target.getName())) {
							yesPvp.remove(target.getName());
							noServer.add(target.getName());
							etc.getServer().messageAll(prefixS + target.getName() + " can't toggle their PVP status now!");
							System.out.println("[SafePVP] " + target.getName() + " has been added to the server no-pvp list.");
						} else if (noServer.contains(target.getName())) {
							System.out.println("[SafePVP] " + target.getName() + " is already on the server no-pvp list.");
						} else {
							noServer.add(target.getName());
							etc.getServer().messageAll(prefixS + target.getName() + " can't toggle their PVP status now!");
							System.out.println("[SafePVP] " + target.getName() + " has been added to the server no-pvp list.");
						}	
					}
				}
			}
			return true;
		}
		return false;
	}



	public boolean onDamage(PluginLoader.DamageType type, BaseEntity att, BaseEntity def, int amount) {
		if (Dm.contains("on")) {
			if (att == null && def != null && amount >= 1 && def.isPlayer()) {
				Player defender = def.getPlayer();

				if (type == PluginLoader.DamageType.FALL && defender.isPlayer() && defender.getHealth()-amount<= 0) {
					etc.getServer().messageAll(prefix + Colors.Rose + defender.getName() + " " + fallMessage);
				}
				if (type == PluginLoader.DamageType.CREEPER_EXPLOSION && defender.isPlayer() && (defender.getHealth()-amount <= 0)) {
					etc.getServer().messageAll(prefix + Colors.Rose + defender.getName() + " " + creeperMessage);
				}
				if (type == PluginLoader.DamageType.EXPLOSION && defender.isPlayer()&& defender.getHealth()-amount<= 0) {
					etc.getServer().messageAll(prefix + Colors.Rose + defender.getName() + " " + tntMessage);
				}
				if (type == PluginLoader.DamageType.WATER && defender.isPlayer() && defender.getHealth()-amount <= 0) {
					etc.getServer().messageAll(prefix + Colors.Rose + defender.getName() + " " + waterMessage);
				}
			}
		}
		if (att != null && def != null && att.isPlayer() && def.isPlayer()) {
			Player attacker = att.getPlayer();
			Player defender = def.getPlayer();

			if (accepted) {
				if (finished) { return false; } else {
					if (sparList.contains(attacker.getName().toLowerCase()) && sparList.contains(defender.getName().toLowerCase())) {
						if (defender.getHealth()-amount <= 0) {
							etc.getServer().messageAll(spar + Colors.Rose + attacker.getName() + " has won the spar between " + defender.getName() + "! Congratulations!");
							finished = true;
							inProgress = false;
							accepted = false;
							valid = false;
							waiting = false;
							sparList.remove(attacker.getName().toLowerCase());
							sparList.remove(defender.getName().toLowerCase());
						}					
						return false;
					}
				}
			}
			if (iAccepted) {
				if (iFinished) { return false; } else {
					if (iSparList.contains(attacker.getName().toLowerCase()) && iSparList.contains(defender.getName().toLowerCase())) {
						if (defender.getHealth()-amount <= 0) {
							etc.getServer().messageAll(duel + Colors.Rose + attacker.getName() + " has won the iSpar between " + defender.getName() + "!");
							etc.getServer().messageAll(duel + Colors.Rose + attacker.getName() + " has been credited " + iWin + " " + currency + "!");
							int balance = Hooked.getInt("iBalance", new Object[] { "balance", attacker.getName() });
							int newBalance = (balance+iWin);
							Hooked.silent("iBalance", new Object[] { "set", attacker.getName(), newBalance }); 
							iFinished = true;
							iInProgress = false;
							iAccepted = false;
							iValid = false;
							iWaiting = false;
							iSparList.remove(attacker.getName().toLowerCase());
							iSparList.remove(defender.getName().toLowerCase());
						}					
						return false;
					}
				}
			}
			if (sparList.contains(defender.getName().toLowerCase()) && accepted) {
				attacker.sendMessage(spar + Colors.Gold + defender.getName() + Colors.White + " is participating in a spar and you aren't their partner.");			
				return true;
			}
			if (sparList.contains(attacker.getName().toLowerCase()) && accepted) {
				attacker.sendMessage(spar + "You are sparring and " + defender.getName() + " isn't your partner.");			
				return true;
			}
			if (iSparList.contains(defender.getName().toLowerCase()) && iAccepted) {
				attacker.sendMessage(duel + Colors.Gold + defender.getName() + Colors.White + " is participating in an iSpar and you aren't their partner.");			
				return true;
			}
			if (iSparList.contains(attacker.getName().toLowerCase()) && iAccepted) {
				attacker.sendMessage(duel + "You are iSparring and " + defender.getName() + " isn't your partner.");			
				return true;
			}
			if (noPvp.contains(defender.getName())) {
				if (yesMsg.contains(defender.getName())) {
					defender.sendMessage(prefix + "The no PVP list protects you from " + Colors.Gold + attacker.getName() + ".");
				}
				if (yesMsg.contains(attacker.getName())) {
					attacker.sendMessage(prefix + Colors.Gold + defender.getName() + Colors.White + " is on the no PVP list. You can't damage them.");			
				}
				return true;
			}
			if (noPvp.contains(attacker.getName())) {
				if (yesMsg.contains(defender.getName())) {
					defender.sendMessage(prefix + Colors.Gold + attacker.getName() + Colors.White +  " is on the no PVP list. You can't be damaged.");
				}
				if (yesMsg.contains(attacker.getName())) {
					attacker.sendMessage(prefix + "You are on the no PVP list. You can not damage!");						
				}
				return true;
			}
			if (!noPvp.contains(attacker.getName()) && !yesPvp.contains(attacker.getName())) {
				if (yesMsg.contains(attacker.getName())) {
					attacker.sendMessage(prefix + "Type /pvp on to battle!");
				}
				return true;
			}
			if (!noPvp.contains(defender.getName()) && !yesPvp.contains(defender.getName())) {
				if (yesMsg.contains(attacker.getName())) {
					attacker.sendMessage(prefix + Colors.Gold + defender.getName() + Colors.White + " hasn't enabled PVP yet!");
				}
				if (yesMsg.contains(defender.getName())) {
					defender.sendMessage(prefix + "Type /pvp on to battle or /pvp off to stay safe.");
				}
				return true;
			}
			if (yesPvp.contains(attacker.getName()) && (yesPvp.contains(defender.getName()))) {
				if (defender.getHealth()-amount <= 0) {
					etc.getServer().messageAll(prefix + Colors.Rose + attacker.getName() + " has just killed " + defender.getName() + ".");
				}
				return false;
			}

		} else {
			return false;
		}
		return false;
	}

}