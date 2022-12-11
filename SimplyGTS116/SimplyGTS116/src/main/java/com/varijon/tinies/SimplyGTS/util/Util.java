package com.varijon.tinies.SimplyGTS.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBall;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.pokemon.stats.Moveset;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.LakeTrioStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.MewStats;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.storage.NbtKeys;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.util.Constants;

public class Util 
{
	

	public static TranslationTextComponent getHoverText(Pokemon pixelmon, ServerPlayerEntity player)
	{
		if (pixelmon != null)
		{
			String pixelmonName = pixelmon.getStats().getPokemon().getSpecies().getName();
			String pixelmonNickname = TextFormatting.stripFormatting(pixelmon.getNickname());
			String pixelmonAbility = pixelmon.getAbility().getLocalizedName();
			String pixelmonGender = pixelmon.getGender().getLocalizedName();
			String pixelmonGrowth = pixelmon.getGrowth().name();
			String pixelmonNature = pixelmon.getNature().getLocalizedName();
			String pixelmonOT = pixelmon.getOriginalTrainer();
			PokeBall pixelmonPokeball = pixelmon.getBall();
			int pixelmonFriendship = pixelmon.getFriendship();
			int pixelmonLevel = pixelmon.getPokemonLevel(); 
			String pixelmonHeldItem = getHeldItem(pixelmon);
			String pixelmonShiny = getShiny(pixelmon.isShiny());
//			String pixelmonCustomTexture = StringUtils.capitalize(pixelmon.getCustomTexture());
//			String pixelmonSpecialTexture = getSpecialTexture(pixelmon);
//			String pixelmonRegionalForm = getRegionalForm(pixelmon);
			
			if(pixelmon.isEgg())
			{
				pixelmonName += " Egg";
				pixelmonAbility = "???";
				pixelmonGender = "???";
				pixelmonGrowth = "???";
				pixelmonNature = "???";
				pixelmonShiny = "???";
			}

			int pixelmonIVHP = pixelmon.getStats().getIVs().getStat(BattleStatsType.HP);
			int pixelmonIVAttack = pixelmon.getStats().getIVs().getStat(BattleStatsType.ATTACK);
			int pixelmonIVDefense = pixelmon.getStats().getIVs().getStat(BattleStatsType.DEFENSE);
			int pixelmonIVSpAtt = pixelmon.getStats().getIVs().getStat(BattleStatsType.SPECIAL_ATTACK);
			int pixelmonIVSpDef = pixelmon.getStats().getIVs().getStat(BattleStatsType.SPECIAL_DEFENSE);
			int pixelmonIVSpeed = pixelmon.getStats().getIVs().getStat(BattleStatsType.SPEED);
			
			int pixelmonEVHP = pixelmon.getStats().getEVs().getStat(BattleStatsType.HP);
			int pixelmonEVAttack = pixelmon.getStats().getEVs().getStat(BattleStatsType.ATTACK);
			int pixelmonEVDefense = pixelmon.getStats().getEVs().getStat(BattleStatsType.DEFENSE);
			int pixelmonEVSpAtt = pixelmon.getStats().getEVs().getStat(BattleStatsType.SPECIAL_ATTACK);
			int pixelmonEVSpDef = pixelmon.getStats().getEVs().getStat(BattleStatsType.SPECIAL_DEFENSE);
			int pixelmonEVSpeed = pixelmon.getStats().getEVs().getStat(BattleStatsType.SPEED);
			
			
			int pixelmonStatsHP = pixelmon.getStats().getHP();
			int pixelmonStatsAttack = pixelmon.getStats().getAttack();
			int pixelmonStatsDefense = pixelmon.getStats().getDefense();
			int pixelmonStatsSpAtt = pixelmon.getStats().getSpecialAttack();
			int pixelmonStatsSpDef = pixelmon.getStats().getSpecialDefense();
			int pixelmonStatsSpeed = pixelmon.getStats().getSpeed();
			

			Moveset pixelmonMoves = pixelmon.getMoveset();
			
			ItemStack item = new ItemStack(PixelmonItems.pixelmon_sprite, 1);									
			item.setTag(new CompoundNBT());
			
			CompoundNBT tags = item.getTag();
			tags.put("display", new CompoundNBT());
			
//				tags.getCompoundTag("display").setString("Name", (pixelmon.isShiny() ? TextFormatting.YELLOW : TextFormatting.GOLD)
//						+ (pixelmonRegionalForm != "" ? pixelmonRegionalForm + " " : "") + pixelmonName);
			tags.getCompound("display").putString("Name", getPokemonDisplayName(pixelmon));
//						+ (pixelmonCustomTexture != "" ? "-" + pixelmonCustomTexture : ""));
			if(checkForHiddenAbility(pixelmon) && !pixelmon.isEgg())
			{
				pixelmonAbility += TextFormatting.WHITE + " (" + TextFormatting.GOLD + "HA" + TextFormatting.WHITE + ")";
			}
			ListNBT loreList = new ListNBT();		
			
			if(pixelmonNickname != null)
			{
				if(!pixelmonName.equals(pixelmonNickname) && !pixelmonNickname.equals(""))
				{
					loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Nickname: " + TextFormatting.GREEN + pixelmonNickname));					
				}
			}
			loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Nature: " + TextFormatting.GREEN + pixelmonNature + getMintNature(pixelmon)));
			loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Ability: " + TextFormatting.GREEN + pixelmonAbility));

			if(!pixelmon.isEgg())
			{
				CompoundNBT pixelmonNBT = pixelmon.writeToNBT(new CompoundNBT());
				if(pixelmonNBT.getByte("GigantamaxFactor") == 1)
				{
					loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Level: " + TextFormatting.GREEN + pixelmonLevel + TextFormatting.GRAY + " -- " + TextFormatting.BLUE + "G-max Level: " + TextFormatting.RED + pixelmon.getDynamaxLevel()));
				}
				else
				{
					loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Level: " + TextFormatting.GREEN + pixelmonLevel + TextFormatting.GRAY + " -- " + TextFormatting.BLUE + "Dynamax Level: " + TextFormatting.GREEN + pixelmon.getDynamaxLevel()));				
				}
			}
			else
			{
				loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Level: " + TextFormatting.GREEN + pixelmonLevel));
			}
			
			
			loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Gender: " + TextFormatting.GREEN + pixelmonGender));
			String auraParticle = "";
			if(pixelmon.getPersistentData().contains("entity-particles:particle"))
			{
				auraParticle = pixelmon.getPersistentData().getString("entity-particles:particle");
			}
			
			loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Shiny: " + TextFormatting.YELLOW + pixelmonShiny + (auraParticle != "" ? TextFormatting.GRAY + " (" + TextFormatting.GOLD + auraParticle + " Aura" + TextFormatting.GRAY + ")" : "")));
			loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Size: " + TextFormatting.GREEN + pixelmonGrowth));
			loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Pokeball: " + TextFormatting.GREEN + pixelmonPokeball.getBallItem().getHoverName().getString()));
//			loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Friendship: " + TextFormatting.GREEN + pixelmonFriendship));
			loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "OT: " + TextFormatting.GREEN + pixelmonOT));
			loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Item: " + TextFormatting.GREEN + pixelmonHeldItem));
			
			if(pixelmon.getExtraStats() instanceof MewStats)
			{
				loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Times Cloned: " + TextFormatting.GREEN + ((MewStats) pixelmon.getExtraStats()).numCloned));
			}
			if(pixelmon.getExtraStats() instanceof LakeTrioStats)
			{
				loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Rubies Extracted: " + TextFormatting.GREEN + ((LakeTrioStats) pixelmon.getExtraStats()).numEnchanted));
			}
//			if(pixelmonCustomTexture != "" && !pixelmon.isEgg())
//			{
//				loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Texture: " + TextFormatting.GREEN + pixelmonCustomTexture));
//			}
//			if(pixelmonSpecialTexture != "" && !pixelmon.isEgg())
//			{
//				loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Form: " + TextFormatting.GREEN + pixelmonSpecialTexture));
//			}
			int combinedIV = pixelmonIVHP + pixelmonIVAttack + pixelmonIVDefense + pixelmonIVSpAtt + pixelmonIVSpDef + pixelmonIVSpeed;
			String IVPercent = TextFormatting.GRAY + "(" + TextFormatting.YELLOW + Math.round(((float)combinedIV / 186.0 * 100.0)) + "% IV" + TextFormatting.GRAY + ")";
			loreList.add(StringNBT.valueOf(TextFormatting.GRAY + "Stats: " + IVPercent));
			loreList.add(StringNBT.valueOf(TextFormatting.LIGHT_PURPLE + "HP: IV: " + TextFormatting.GREEN + pixelmonIVHP + TextFormatting.LIGHT_PURPLE + " EV: " + TextFormatting.GREEN + pixelmonEVHP + TextFormatting.LIGHT_PURPLE + " Stat: " + TextFormatting.GREEN + pixelmonStatsHP + (pixelmon.getIVs().isHyperTrained(BattleStatsType.HP) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : "")));
			loreList.add(StringNBT.valueOf(TextFormatting.RED + "Atk: IV: " + TextFormatting.GREEN + pixelmonIVAttack + TextFormatting.RED + " EV: " + TextFormatting.GREEN + pixelmonEVAttack + TextFormatting.RED + " Stat: " + TextFormatting.GREEN + pixelmonStatsAttack + (pixelmon.getIVs().isHyperTrained(BattleStatsType.ATTACK) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : "")));
			loreList.add(StringNBT.valueOf(TextFormatting.GOLD + "Def: IV: " + TextFormatting.GREEN + pixelmonIVDefense + TextFormatting.GOLD + " EV: " + TextFormatting.GREEN + pixelmonEVDefense + TextFormatting.GOLD + " Stat: " + TextFormatting.GREEN + pixelmonStatsDefense + (pixelmon.getIVs().isHyperTrained(BattleStatsType.DEFENSE) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : "")));
			loreList.add(StringNBT.valueOf(TextFormatting.DARK_PURPLE + "SpAtt: IV: " + TextFormatting.GREEN + pixelmonIVSpAtt + TextFormatting.DARK_PURPLE + " EV: " + TextFormatting.GREEN + pixelmonEVSpAtt + TextFormatting.DARK_PURPLE + " Stat: " + TextFormatting.GREEN + pixelmonStatsSpAtt + (pixelmon.getIVs().isHyperTrained(BattleStatsType.SPECIAL_ATTACK) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : "")));
			loreList.add(StringNBT.valueOf(TextFormatting.YELLOW + "SpDef: IV: " + TextFormatting.GREEN + pixelmonIVSpDef + TextFormatting.YELLOW + " EV: " + TextFormatting.GREEN + pixelmonEVSpDef + TextFormatting.YELLOW + " Stat: " + TextFormatting.GREEN + pixelmonStatsSpDef + (pixelmon.getIVs().isHyperTrained(BattleStatsType.SPECIAL_DEFENSE) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : "")));
			loreList.add(StringNBT.valueOf(TextFormatting.AQUA + "Speed: IV: " + TextFormatting.GREEN + pixelmonIVSpeed + TextFormatting.AQUA + " EV: " + TextFormatting.GREEN + pixelmonEVSpeed + TextFormatting.AQUA + " Stat: " + TextFormatting.GREEN + pixelmonStatsSpeed + (pixelmon.getIVs().isHyperTrained(BattleStatsType.SPEED) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : "")));
			
			loreList.add(StringNBT.valueOf(TextFormatting.DARK_PURPLE + "Moves:"));
			
			
			if(!pixelmon.isEgg())
			{
				for(Attack move : pixelmonMoves)
				{
					loreList.add(StringNBT.valueOf(TextFormatting.GREEN + move.getActualMove().getLocalizedName()));
				}
			}
			else
			{
				loreList.add(StringNBT.valueOf(TextFormatting.GREEN + "???"));
			}
			
			tags.getCompound("display").put("Lore", loreList);
						
			item.setTag(tags);
			
			StringBuilder tooltipString = new StringBuilder();
			tooltipString.append(tags.getCompound("display").getString("Name"));
			
			for(INBT str : loreList)
			{
				tooltipString.append("\n" + ((StringNBT)str).getAsString());
			}
			
	        TranslationTextComponent chatTransComplete = new TranslationTextComponent(TextFormatting.WHITE + "[" + tags.getCompound("display").getString("Name") + TextFormatting.WHITE + "]", new Object[0]);
	        chatTransComplete.setStyle(chatTransComplete.getStyle().withHoverEvent(
	        		new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
	        				new StringTextComponent(tooltipString.toString())
	        				)
	        		));
			
			return chatTransComplete;
		}
		else
		{
			return null;
		}
	}

	public static String getPokemonDisplayName(Pokemon pixelmon) 
	{
		String pixelmonName = pixelmon.getStats().getPokemon().getSpecies().getName();
		return (pixelmon.isShiny() && !pixelmon.isEgg() ? TextFormatting.YELLOW : TextFormatting.GOLD)
		+ pixelmonName + getFormSuffix(pixelmon).replace("%", " Percent");
	}

	public static String getShiny(boolean shinyID)
	{
		String returnShiny = "";
		
		if(shinyID)
		{
			returnShiny = "Yes";			
		}
		else
		{
			returnShiny = "No";
		}
		
		return returnShiny;
	}

	
	public static boolean checkForHiddenAbility(Pokemon pixelmon)
	{
		if(pixelmon.hasHiddenAbility())
		{
			return true;
		}
		return false;
	}

	public static String getFormSuffix(Pokemon pixelmon)
	{		
		StringBuilder sb = new StringBuilder();
		if(pixelmon.isDefaultForm() && pixelmon.isDefaultPalette())
		{
			return "";
		}
		if(!pixelmon.isDefaultForm())
		{
			sb.append("-" + pixelmon.getForm().getLocalizedName());
		}
		if(!pixelmon.isDefaultPalette())
		{
			sb.append("-" + pixelmon.getPalette().getLocalizedName());				
		}
			
		return sb.toString();
	}
	
	
	public static String getMintNature(Pokemon pixelmon)
	{
		if(pixelmon.getMintNature() != null)
		{
			return TextFormatting.WHITE + " (" + TextFormatting.GRAY + pixelmon.getBaseNature().name() + TextFormatting.WHITE + ")"; 
		}
		return "";
	}

	
	public static String getHeldItem(Pokemon pixelmon)
	{
		if(pixelmon.getHeldItem() != null)
		{
			if(pixelmon.getHeldItem().getItem() != Items.AIR)
			{
				return pixelmon.getHeldItem().getHoverName().getString();
			}
		}
		return "None";
	}
	
	public static TranslationTextComponent getHoverText(int partySlot, ServerPlayerEntity player, boolean toAll) 
	{
		PlayerPartyStorage playerStorage =  StorageProxy.getParty(player);
		
		int partyNumber = partySlot - 1;
		
		Pokemon pixelmon = playerStorage.get(partyNumber);
		if (pixelmon != null)
		{		
			String pixelmonName = pixelmon.getStats().getPokemon().getSpecies().getName();
			String pixelmonNickname = TextFormatting.stripFormatting(pixelmon.getNickname());
			String pixelmonAbility = pixelmon.getAbility().getLocalizedName();
			String pixelmonGender = pixelmon.getGender().getLocalizedName();
			String pixelmonGrowth = pixelmon.getGrowth().name();
			String pixelmonNature = pixelmon.getNature().name();
			String pixelmonOT = pixelmon.getOriginalTrainer();
			PokeBall pixelmonPokeball = pixelmon.getBall();
			int pixelmonFriendship = pixelmon.getFriendship();
			int pixelmonLevel = pixelmon.getPokemonLevel(); 
			String pixelmonHeldItem = getHeldItem(pixelmon);
			String pixelmonShiny = getShiny(pixelmon.isShiny());
//			String pixelmonCustomTexture = StringUtils.capitalize(pixelmon.get);
//			String pixelmonSpecialTexture = getSpecialTexture(pixelmon);
//			String pixelmonRegionalForm = getRegionalForm(pixelmon);
			
			if(pixelmon.isEgg())
			{
				pixelmonName += " Egg";
				pixelmonAbility = "???";
				pixelmonGender = "???";
				pixelmonGrowth = "???";
				pixelmonNature = "???";
				pixelmonShiny = "???";
			}
					
			int pixelmonIVHP = pixelmon.getStats().getIVs().getStat(BattleStatsType.HP);
			int pixelmonIVAttack = pixelmon.getStats().getIVs().getStat(BattleStatsType.ATTACK);
			int pixelmonIVDefense = pixelmon.getStats().getIVs().getStat(BattleStatsType.DEFENSE);
			int pixelmonIVSpAtt = pixelmon.getStats().getIVs().getStat(BattleStatsType.SPECIAL_ATTACK);
			int pixelmonIVSpDef = pixelmon.getStats().getIVs().getStat(BattleStatsType.SPECIAL_DEFENSE);
			int pixelmonIVSpeed = pixelmon.getStats().getIVs().getStat(BattleStatsType.SPEED);
			
			int pixelmonEVHP = pixelmon.getStats().getEVs().getStat(BattleStatsType.HP);
			int pixelmonEVAttack = pixelmon.getStats().getEVs().getStat(BattleStatsType.ATTACK);
			int pixelmonEVDefense = pixelmon.getStats().getEVs().getStat(BattleStatsType.DEFENSE);
			int pixelmonEVSpAtt = pixelmon.getStats().getEVs().getStat(BattleStatsType.SPECIAL_ATTACK);
			int pixelmonEVSpDef = pixelmon.getStats().getEVs().getStat(BattleStatsType.SPECIAL_DEFENSE);
			int pixelmonEVSpeed = pixelmon.getStats().getEVs().getStat(BattleStatsType.SPEED);
			
			
			int pixelmonStatsHP = pixelmon.getStats().getHP();
			int pixelmonStatsAttack = pixelmon.getStats().getAttack();
			int pixelmonStatsDefense = pixelmon.getStats().getDefense();
			int pixelmonStatsSpAtt = pixelmon.getStats().getSpecialAttack();
			int pixelmonStatsSpDef = pixelmon.getStats().getSpecialDefense();
			int pixelmonStatsSpeed = pixelmon.getStats().getSpeed();
			
			Moveset pixelmonMoves = pixelmon.getMoveset();
			
			String megaForm = "";
			String formSuffix = getFormSuffix(pixelmon).replace("-", "");
			
			if(pixelmon.isMega())
			{
				StringBuilder sb = new StringBuilder();
				sb.append((pixelmon.isShiny() ? TextFormatting.YELLOW : TextFormatting.GOLD) + "Mega ");
				sb.append(pixelmonName);
				megaForm = sb.toString();
			}
			String auraParticle = "";
			if(pixelmon.getPersistentData().contains("entity-particles:particle"))
			{
				auraParticle = pixelmon.getPersistentData().getString("entity-particles:particle");
			}
			
			String extraStats = "";
			if(pixelmon.getExtraStats() instanceof MewStats)
			{
				extraStats = "Times Cloned: " + ((MewStats) pixelmon.getExtraStats()).numCloned;
			}
			if(pixelmon.getExtraStats() instanceof LakeTrioStats)
			{
				extraStats = "Rubies Extracted: " + ((LakeTrioStats) pixelmon.getExtraStats()).numEnchanted;
			}
			
			if(checkForHiddenAbility(pixelmon) && !pixelmon.isEgg())
			{
				pixelmonAbility += TextFormatting.WHITE + " (" + TextFormatting.GOLD + "HA" + TextFormatting.WHITE + ")";
			}
					
	        TranslationTextComponent info = new TranslationTextComponent("[Info]", new Object[0]);
	        info.getStyle().withColor(TextFormatting.RED).withHoverEvent(
	        		new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
	        				new StringTextComponent(
	        						"" + TextFormatting.DARK_GREEN + TextFormatting.UNDERLINE + (pixelmonNickname == null ? pixelmonName : pixelmonNickname) 
	        						+ (pixelmon.isShiny() ? new StringBuilder().append(TextFormatting.YELLOW).append("\u2605").toString() : "") + "\n" 
	        						+ TextFormatting.AQUA + "Level: " + pixelmonLevel + "\n" 
	        						+ TextFormatting.YELLOW + "Nature: " + pixelmonNature + getMintNature(pixelmon) + "\n" 	        						
	        						+ TextFormatting.GREEN + "Growth: " + pixelmonGrowth + "\n" 
	        						+ TextFormatting.GOLD + "Ability: " + pixelmonAbility + "\n" 
	        						+ TextFormatting.LIGHT_PURPLE + "Gender: " + pixelmonGender + "\n" 
	        						+ TextFormatting.DARK_PURPLE + "OT: " + pixelmonOT + "\n" 
	        						+ TextFormatting.RED + "Item: " + pixelmonHeldItem
	        						)
	        				)
	        		);
	
	        TranslationTextComponent ivs = new TranslationTextComponent("[IVs]", new Object[0]);
	        ivs.getStyle().withColor(TextFormatting.LIGHT_PURPLE).withHoverEvent(
	        		new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
	        				new StringTextComponent(
			        				"" + TextFormatting.LIGHT_PURPLE + TextFormatting.UNDERLINE + "IVs" + "\n" 
			        				+ TextFormatting.LIGHT_PURPLE + "HP: " + pixelmonIVHP + (pixelmon.getIVs().isHyperTrained(BattleStatsType.HP) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : "") + "\n" 
			        				+ TextFormatting.LIGHT_PURPLE + "Attack: " + pixelmonIVAttack + (pixelmon.getIVs().isHyperTrained(BattleStatsType.ATTACK) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : "") + "\n" 
			        				+ TextFormatting.LIGHT_PURPLE + "Defence: " + pixelmonIVDefense + (pixelmon.getIVs().isHyperTrained(BattleStatsType.DEFENSE) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : "") + "\n" 
			        				+ TextFormatting.LIGHT_PURPLE + "Sp. Attack: " + pixelmonIVSpAtt + (pixelmon.getIVs().isHyperTrained(BattleStatsType.SPECIAL_ATTACK) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : "") + "\n" 
			        				+ TextFormatting.LIGHT_PURPLE + "Sp. Defence: " + pixelmonIVSpDef + (pixelmon.getIVs().isHyperTrained(BattleStatsType.SPECIAL_DEFENSE) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : "") + "\n" 
			        				+ TextFormatting.LIGHT_PURPLE + "Speed: " + pixelmonIVSpeed + (pixelmon.getIVs().isHyperTrained(BattleStatsType.SPEED) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : "")
			        				)
	        				)
	        		);
			TranslationTextComponent evs = new TranslationTextComponent("[EVs]", new Object[0]);
	        evs.getStyle().withColor(TextFormatting.GOLD).withHoverEvent(
	        		new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
	        				new StringTextComponent(
	        						"" + TextFormatting.GOLD + TextFormatting.UNDERLINE + "EVs" + "\n" 
	        						+ TextFormatting.GOLD + "HP: " + pixelmonEVHP + "\n" 
	        						+ TextFormatting.GOLD + "Attack: " + pixelmonEVAttack + "\n" 
	        						+ TextFormatting.GOLD + "Defence: " + pixelmonEVDefense + "\n" 
	        						+ TextFormatting.GOLD + "Sp. Attack: " + pixelmonEVSpAtt + "\n" 
	        						+ TextFormatting.GOLD + "Sp. Defence: " + pixelmonEVSpDef + "\n"
	        						+ TextFormatting.GOLD + "Speed: " + pixelmonEVSpeed
	        						)
	        				)
	        		);
	        StringBuilder moveString = new StringBuilder();
	        moveString.append("" + TextFormatting.BLUE + TextFormatting.UNDERLINE + "Moves");
	        if(!pixelmon.isEgg())
			{
				for(Attack move : pixelmonMoves)
				{
					moveString.append("\n" + TextFormatting.BLUE + move.getActualMove().getLocalizedName());
				}
			}
			else
			{
				moveString.append("\n" + TextFormatting.BLUE + "???");
			}
	        
	        TranslationTextComponent moves = new TranslationTextComponent("[Moves]", new Object[0]);
	        moves.getStyle().withColor(TextFormatting.BLUE).withHoverEvent(
	        		new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
	        				new StringTextComponent(moveString.toString())
	        				)
	        		);
	        
	        TranslationTextComponent extra = new TranslationTextComponent("[Extra]", new Object[0]);
	        extra.getStyle().withColor(TextFormatting.DARK_AQUA).withHoverEvent(
	        		new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
	        				new StringTextComponent(
	        						"" + TextFormatting.DARK_AQUA + TextFormatting.UNDERLINE + "Extra" + "\n" 
	        						+ TextFormatting.GOLD + "Pokeball: " + pixelmonPokeball.getName()
	        						+ (!megaForm.equals("") ? "\n" + TextFormatting.RED + "Mega: " + megaForm : "")
	    	        				+ (!formSuffix.equals("") ? "\n" +  TextFormatting.GREEN + "Form: " + formSuffix: "") 
//	        						+ (!pixelmonCustomTexture.equals("") ? "\n" +  TextFormatting.AQUA + "Texture: " + pixelmonCustomTexture: "") 
	        						+ (!auraParticle.equals("") ? "\n" +  TextFormatting.YELLOW + "Aura: " + auraParticle : "")
	        						+ (!extraStats.equals("") ? "\n" +  TextFormatting.BLUE + extraStats : "")  
	        						)
	        				)
	        		);
	        
	        TranslationTextComponent chatTransFinal = new TranslationTextComponent("", new Object());
	        if(toAll)
	        {
		        chatTransFinal.append(new StringTextComponent(TextFormatting.AQUA + player.getName().getString() + "'s Pokemon " + TextFormatting.GRAY + "(" + TextFormatting.GOLD + "To All" + TextFormatting.GRAY + ")\n"));	        	
	        }
	        else
	        {
		        chatTransFinal.append(new StringTextComponent(TextFormatting.AQUA + player.getName().getString() + "'s Pokemon " + TextFormatting.GRAY + "(" + TextFormatting.GOLD + "To You" + TextFormatting.GRAY + ")\n"));	        	
	        }
	        chatTransFinal.append(new TranslationTextComponent(
	        		TextFormatting.GREEN + ((pixelmon.isShiny() ? TextFormatting.YELLOW + pixelmonName + new StringBuilder().append(TextFormatting.YELLOW).append("\u2605").toString() : ""  + TextFormatting.GREEN + pixelmonName) 
	        		+ " " + "%s" + " " + "%s" + " " + "%s" + " " + "%s" + " " + "%s"), new Object[]{info, evs, ivs, moves, extra}));
	        return chatTransFinal;
		}
		else
		{
			return null;
		}
        
    }
	public static TextFormatting colorIV(int pIV)
	{
		if(pIV < 6)
		{
			return TextFormatting.DARK_RED;
		}
		if(pIV < 11)
		{
			return TextFormatting.RED;
		}
		if(pIV < 16)
		{
			return TextFormatting.GOLD;
		}
		if(pIV < 23)
		{
			return TextFormatting.DARK_GREEN;
		}
		if(pIV < 31)
		{
			return TextFormatting.GREEN;
		}
		if(pIV == 31)
		{
			return TextFormatting.BLUE;
		}
		return TextFormatting.GRAY;
	}
	
	
	
}
