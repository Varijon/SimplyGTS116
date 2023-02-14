package com.varijon.tinies.SimplyGTS.gui;

import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBall;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.pokemon.stats.Moveset;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.LakeTrioStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.MewStats;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.storage.NbtKeys;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.varijon.tinies.SimplyGTS.object.GTSListingPokemon;
import com.varijon.tinies.SimplyGTS.util.Util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient.TagList;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.UsernameCache;

public class PokemonListingDisplay
{
	public static ArrayList<String> getPokemonListingDisplayList(Pokemon pixelmon, UUID player, GTSListingPokemon gtsListingPokemon, boolean isCancel)
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
			String pixelmonHeldItem = Util.getHeldItem(pixelmon);
			String pixelmonShiny = Util.getShiny(pixelmon.isShiny());
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
			
			
			if(Util.checkForHiddenAbility(pixelmon) && !pixelmon.isEgg())
			{
				pixelmonAbility += TextFormatting.WHITE + " (" + TextFormatting.GOLD + "HA" + TextFormatting.WHITE + ")";
			}
			ArrayList<String> loreList = new ArrayList<String>();
			String playerName = UsernameCache.getLastKnownUsername(gtsListingPokemon.getListingOwner());

			if(playerName == null)
			{
				playerName = "Someone";
			}
			if(gtsListingPokemon.getListingOwner().equals(player) && !isCancel)
			{
				loreList.add(TextFormatting.GREEN + "Click here to cancel listing!");
			}
			else
			{
				loreList.add(TextFormatting.RED + "Seller: " + TextFormatting.GOLD + playerName);
			}
			loreList.add(TextFormatting.RED + "Price: " + TextFormatting.GOLD + gtsListingPokemon.getListingPrice());
			loreList.add(TextFormatting.RED + "Remaining: " + TextFormatting.GOLD + DurationFormatUtils.formatDuration(gtsListingPokemon.getListingTimeRemaining(),"dd'd 'HH'h 'mm'm 'ss's'", false));
			
			if(pixelmonNickname != null)
			{
				if(!pixelmonName.equals(pixelmonNickname) && !pixelmonNickname.equals(""))
				{
					loreList.add(TextFormatting.BLUE + "Nickname: " + TextFormatting.GREEN + pixelmonNickname);					
				}
			}
			loreList.add(TextFormatting.BLUE + "Nature: " + TextFormatting.GREEN + pixelmonNature + Util.getMintNature(pixelmon));
			loreList.add(TextFormatting.BLUE + "Ability: " + TextFormatting.GREEN + pixelmonAbility);
			
			if(!pixelmon.isEgg())
			{
				CompoundNBT pixelmonNBT = pixelmon.writeToNBT(new CompoundNBT());
				if(pixelmonNBT.getByte("GigantamaxFactor") == 1)
				{
					loreList.add(TextFormatting.BLUE + "Level: " + TextFormatting.GREEN + pixelmonLevel + TextFormatting.GRAY + " -- " + TextFormatting.BLUE + "G-max Level: " + TextFormatting.RED + pixelmon.getDynamaxLevel());
				}
				else
				{
					loreList.add(TextFormatting.BLUE + "Level: " + TextFormatting.GREEN + pixelmonLevel + TextFormatting.GRAY + " -- " + TextFormatting.BLUE + "Dynamax Level: " + TextFormatting.GREEN + pixelmon.getDynamaxLevel());				
				}
			}
			else
			{
				loreList.add(TextFormatting.BLUE + "Level: " + TextFormatting.GREEN + pixelmonLevel);
			}
			
			
			loreList.add(TextFormatting.BLUE + "Gender: " + TextFormatting.GREEN + pixelmonGender + TextFormatting.WHITE + " (" + (gtsListingPokemon.isSoldAsBreedable() ? TextFormatting.AQUA + "Breedable" : TextFormatting.RED + "Unbreedable")  + TextFormatting.WHITE + ")" );
			String auraParticle = "";
			if(pixelmon.getPersistentData().contains("entity-particles:particle"))
			{
				auraParticle = pixelmon.getPersistentData().getString("entity-particles:particle");
			}
			
			loreList.add(TextFormatting.BLUE + "Shiny: " + TextFormatting.YELLOW + pixelmonShiny + (auraParticle != "" ? TextFormatting.GRAY + " (" + TextFormatting.GOLD + auraParticle + " Aura" + TextFormatting.GRAY + ")" : ""));
			loreList.add(TextFormatting.BLUE + "Size: " + TextFormatting.GREEN + pixelmonGrowth);
			loreList.add(TextFormatting.BLUE + "Pokeball: " + TextFormatting.GREEN + pixelmonPokeball.getBallItem().getHoverName().getString());
//			loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Friendship: " + TextFormatting.GREEN + pixelmonFriendship));
			loreList.add(TextFormatting.BLUE + "OT: " + TextFormatting.GREEN + pixelmonOT);
			loreList.add(TextFormatting.BLUE + "Item: " + TextFormatting.GREEN + pixelmonHeldItem);
			
			if(pixelmon.getExtraStats() instanceof MewStats)
			{
				loreList.add(TextFormatting.BLUE + "Times Cloned: " + TextFormatting.GREEN + ((MewStats) pixelmon.getExtraStats()).numCloned);
			}
			if(pixelmon.getExtraStats() instanceof LakeTrioStats)
			{
				loreList.add(TextFormatting.BLUE + "Rubies Extracted: " + TextFormatting.GREEN + ((LakeTrioStats) pixelmon.getExtraStats()).numEnchanted);
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
			loreList.add(TextFormatting.GRAY + "Stats: " + IVPercent);
			loreList.add(TextFormatting.LIGHT_PURPLE + "HP: IV: " + TextFormatting.GREEN + pixelmonIVHP + TextFormatting.LIGHT_PURPLE + " EV: " + TextFormatting.GREEN + pixelmonEVHP + TextFormatting.LIGHT_PURPLE + " Stat: " + TextFormatting.GREEN + pixelmonStatsHP + (pixelmon.getIVs().isHyperTrained(BattleStatsType.HP) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : ""));
			loreList.add(TextFormatting.RED + "Atk: IV: " + TextFormatting.GREEN + pixelmonIVAttack + TextFormatting.RED + " EV: " + TextFormatting.GREEN + pixelmonEVAttack + TextFormatting.RED + " Stat: " + TextFormatting.GREEN + pixelmonStatsAttack + (pixelmon.getIVs().isHyperTrained(BattleStatsType.ATTACK) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : ""));
			loreList.add(TextFormatting.GOLD + "Def: IV: " + TextFormatting.GREEN + pixelmonIVDefense + TextFormatting.GOLD + " EV: " + TextFormatting.GREEN + pixelmonEVDefense + TextFormatting.GOLD + " Stat: " + TextFormatting.GREEN + pixelmonStatsDefense + (pixelmon.getIVs().isHyperTrained(BattleStatsType.DEFENSE) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : ""));
			loreList.add(TextFormatting.DARK_PURPLE + "SpAtt: IV: " + TextFormatting.GREEN + pixelmonIVSpAtt + TextFormatting.DARK_PURPLE + " EV: " + TextFormatting.GREEN + pixelmonEVSpAtt + TextFormatting.DARK_PURPLE + " Stat: " + TextFormatting.GREEN + pixelmonStatsSpAtt + (pixelmon.getIVs().isHyperTrained(BattleStatsType.SPECIAL_ATTACK) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : ""));
			loreList.add(TextFormatting.YELLOW + "SpDef: IV: " + TextFormatting.GREEN + pixelmonIVSpDef + TextFormatting.YELLOW + " EV: " + TextFormatting.GREEN + pixelmonEVSpDef + TextFormatting.YELLOW + " Stat: " + TextFormatting.GREEN + pixelmonStatsSpDef + (pixelmon.getIVs().isHyperTrained(BattleStatsType.SPECIAL_DEFENSE) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : ""));
			loreList.add(TextFormatting.AQUA + "Speed: IV: " + TextFormatting.GREEN + pixelmonIVSpeed + TextFormatting.AQUA + " EV: " + TextFormatting.GREEN + pixelmonEVSpeed + TextFormatting.AQUA + " Stat: " + TextFormatting.GREEN + pixelmonStatsSpeed + (pixelmon.getIVs().isHyperTrained(BattleStatsType.SPEED) ? TextFormatting.WHITE + " (" + TextFormatting.AQUA + "HT" + TextFormatting.WHITE + ")" : ""));
			
			loreList.add(TextFormatting.DARK_PURPLE + "Moves:");
			
			
			if(!pixelmon.isEgg())
			{
				for(Attack move : pixelmonMoves)
				{
					loreList.add(TextFormatting.GREEN + move.getActualMove().getLocalizedName());
				}
			}
			else
			{
				loreList.add(TextFormatting.GREEN + "???");
			}
			
			return loreList;
		}
		else
		{
			return null;
		}
	}
}
