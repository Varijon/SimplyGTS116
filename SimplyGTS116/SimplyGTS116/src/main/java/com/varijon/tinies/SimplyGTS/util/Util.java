package com.varijon.tinies.SimplyGTS.util;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

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
import com.pixelmonmod.pixelmon.entities.npcs.NPCShopkeeper;
import com.pixelmonmod.pixelmon.entities.npcs.registry.BaseShopItem;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ServerNPCRegistry;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopItem;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopItemWithVariation;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopkeeperData;
import com.varijon.tinies.SimplyGTS.enums.EnumSortingOption;
import com.varijon.tinies.SimplyGTS.object.GTSItemPriceHistory;
import com.varijon.tinies.SimplyGTS.object.GTSListingHistory;
import com.varijon.tinies.SimplyGTS.object.GTSListingItem;
import com.varijon.tinies.SimplyGTS.object.GTSListingPokemon;
import com.varijon.tinies.SimplyGTS.object.GTSPriceHistoryList;
import com.varijon.tinies.SimplyGTS.object.ItemConfigMinPrice;
import com.varijon.tinies.SimplyGTS.storage.GTSDataManager;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack.TooltipDisplayFlags;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.common.util.Constants;

public class Util 
{

	private static final Pattern periodPattern = Pattern.compile("([0-9]+)([hdwmy])");

	public static TranslationTextComponent getHoverText(GTSListingPokemon gtsListingPokemon, ServerPlayerEntity player)
	{
		Pokemon pixelmon = gtsListingPokemon.createOrGetPokemonData();
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
			
			
			loreList.add(StringNBT.valueOf(TextFormatting.BLUE + "Gender: " + TextFormatting.GREEN + pixelmonGender + TextFormatting.WHITE + " (" + (gtsListingPokemon.isSoldAsBreedable() ? TextFormatting.AQUA + "Breedable" : TextFormatting.RED + "Unbreedable")  + TextFormatting.WHITE + ")" ));
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
			sb.append("-" + StringUtils.capitalize(pixelmon.getForm().getLocalizedName().split("-")[0]));
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
	
	public static int calculateMinimumPriceItem(ItemStack item)
	{
		int finalPrice = 0;
		
		for(ItemConfigMinPrice itemConfig : GTSDataManager.getConfig().getLstMinItemPrices())
		{
			if(itemConfig.getItemName().equals(item.getItem().getRegistryName().toString()))
			{
				if(item.getTag() != null)
				{
					if(item.getTag().toString().equals(itemConfig.getItemNBT()))
					{
						finalPrice += itemConfig.getMinPrice();
					}
				}
				else
				{
					finalPrice += itemConfig.getMinPrice();
				}
			}
		}
//		ShopItemWithVariation shopItem = null;
//		for(ShopkeeperData shopData : ServerNPCRegistry.getEnglishShopkeepers())
//		{
//			for(ShopItemWithVariation shopItemVar : shopData.getItemList())
//			{
//				if(shopItemVar.getItemStack().getItem() == item.getItem())
//				{
//					if(item.getMetadata() == shopItemVar.getItemStack().getMetadata())
//					{
//						if(item.hasTagCompound() && shopItemVar.getItemStack().hasTagCompound())
//						{
//							if(item.getTagCompound().hasKey("tm"))
//							{
//								if(item.getTagCompound().getInteger("tm") == shopItemVar.getItemStack().getTagCompound().getInteger("tm"))
//								{
//									shopItem = shopItemVar;
//									break;
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		if(shopItem != null)
//		{
//			finalPrice += shopItem.getBuyCost() / 4;
//		}
		return finalPrice;
	}
	public static int calculateMinimumPricePokemon(Pokemon pokemon, boolean sellAsBreedable)
	{
		int finalPrice = 0;
		if(Util.checkForHiddenAbility(pokemon))
		{
			finalPrice+= GTSDataManager.getConfig().getMinHAPrice();
		}
		int pixelmonIVHP = pokemon.getStats().getIVs().getStat(BattleStatsType.HP);
		int pixelmonIVAttack = pokemon.getStats().getIVs().getStat(BattleStatsType.ATTACK);
		int pixelmonIVDefense = pokemon.getStats().getIVs().getStat(BattleStatsType.DEFENSE);
		int pixelmonIVSpAtt = pokemon.getStats().getIVs().getStat(BattleStatsType.SPECIAL_ATTACK);
		int pixelmonIVSpDef = pokemon.getStats().getIVs().getStat(BattleStatsType.SPECIAL_DEFENSE);
		int pixelmonIVSpeed = pokemon.getStats().getIVs().getStat(BattleStatsType.SPEED);
		
		int maxIVCount = 0;
		if(pixelmonIVHP == 31)
		{
			maxIVCount++;
		}
		if(pixelmonIVAttack == 31)
		{
			maxIVCount++;
		}
		if(pixelmonIVDefense == 31)
		{
			maxIVCount++;
		}
		if(pixelmonIVSpAtt == 31)
		{
			maxIVCount++;
		}
		if(pixelmonIVSpDef == 31)
		{
			maxIVCount++;
		}
		if(pixelmonIVSpeed == 31)
		{
			maxIVCount++;
		}
		if(maxIVCount > 2)
		{
			switch(maxIVCount)
			{
				case(3):
					finalPrice+= GTSDataManager.getConfig().getMinPrice3IV();
					break;
				case(4):
					finalPrice+= GTSDataManager.getConfig().getMinPrice4IV();
					break;
				case(5):
					finalPrice+= GTSDataManager.getConfig().getMinPrice5IV();
					break;
				case(6):
					finalPrice+= GTSDataManager.getConfig().getMinPrice6IV();
					break;
			}
		}
		if(sellAsBreedable)
		{
			finalPrice *= GTSDataManager.getConfig().getBreedablePriceMultiplier();
		}
		if(finalPrice == 0)
		{
			finalPrice = 100;
		}
		
		return finalPrice;
	}
	
	public static long parsePeriod(String period)
	{
	    period = period.toLowerCase(Locale.ENGLISH);
	    Matcher matcher = periodPattern.matcher(period);
	    Instant instant=Instant.EPOCH;
	    while(matcher.find()){
	        int num = Integer.parseInt(matcher.group(1));
	        String typ = matcher.group(2);
	        switch (typ) {
	        	case "m":
	        		instant=instant.plus(Duration.ofMinutes(num));
	        		break;
	            case "h":
	                instant=instant.plus(Duration.ofHours(num));
	                break;
	            case "d":
	                instant=instant.plus(Duration.ofDays(num));
	                break;
	            case "w":
	                instant=instant.plus(Period.ofWeeks(num));
	                break;
	        }
	    }
	    return instant.toEpochMilli();
	}
	
	public static ShopItemWithVariation getShopItem(ItemStack itemToSell)
	{
		ShopItemWithVariation shopItem = null;
		for(ShopkeeperData shopData : ServerNPCRegistry.getEnglishShopkeepers())
		{
			for(ShopItemWithVariation shopItemVar : shopData.getItemList())
			{
				if(shopItemVar.getItemStack().getItem() == itemToSell.getItem())
				{
					if(itemToSell.getTag() != null && shopItemVar.getItemStack().getTag() != null)
					{
						if(NBTUtil.compareNbt(itemToSell.getTag(), shopItemVar.getItemStack().getTag(), true))
						{
							shopItem = shopItemVar;		
							break;											
						}
					}
					else
					{
						shopItem = shopItemVar;
						break;
					}
				}
			}
		}
		return shopItem;
	}
	
	public static ShopItemWithVariation getSellPrice(ItemStack itemToSell)
	{
		ShopItemWithVariation returnItem = null;

		BaseShopItem shopItem = ServerNPCRegistry.shopkeepers.getItem(itemToSell);
		if(shopItem != null)
		{
			returnItem = new ShopItemWithVariation(new ShopItem(ServerNPCRegistry.shopkeepers.getItem(itemToSell), 0, 0, false));			
		}
		
		return returnItem;
	}
	
	public static void registerPriceHistory(GTSListingItem gtsListingItem)
	{
		GTSPriceHistoryList priceHistoryList = GTSDataManager.getPriceHistoryList(gtsListingItem.getItemName());
		if(priceHistoryList == null)
		{
			priceHistoryList = GTSDataManager.addHistoryData(new GTSPriceHistoryList(new ArrayList<GTSItemPriceHistory>(), gtsListingItem.getItemName()));
		}
		GTSItemPriceHistory priceHistory = priceHistoryList.getItemPriceHistory(gtsListingItem.createOrGetItemStack());
		if(priceHistory == null)
		{
			priceHistory = priceHistoryList.addPriceHistory(new GTSItemPriceHistory(0, 0, gtsListingItem.createOrGetItemStack().getOrCreateTag().toString()));
		}
		priceHistory.setNumberSold(priceHistory.getNumberSold() + gtsListingItem.getItemCount());
		priceHistory.setTotalSpent(priceHistory.getTotalSpent() + gtsListingItem.getListingPrice());
		
		GTSDataManager.writeHistoryData(priceHistoryList);
	}
	
	public static String getSortTextAZ(EnumSortingOption sort) 
	{		
		if(sort == EnumSortingOption.AZ)
		{
			return "Click to sort by Z-A";
		}
		if(sort == EnumSortingOption.AZReversed)
		{
			return "Click to sort by A-Z";			
		}
		return "Click to sort by A-Z";
	}
	
	public static EnumSortingOption getReverseSortAZ(EnumSortingOption sort)
	{
		if(sort == EnumSortingOption.AZ)
		{
			return EnumSortingOption.AZReversed;
		}
		if(sort== EnumSortingOption.AZReversed)
		{
			return EnumSortingOption.AZ;
		}
		return EnumSortingOption.AZ;
	}
	
	public static String getSortTextDuration(EnumSortingOption sort) 
	{		
		if(sort == EnumSortingOption.Duration)
		{
			return "Click to sort by oldest";
		}
		if(sort == EnumSortingOption.DurationReversed)
		{
			return "Click to sort by newest";			
		}
		return "Click to sort newest";
	}
	
	public static EnumSortingOption getReverseSortDuration(EnumSortingOption sort)
	{
		if(sort == EnumSortingOption.Duration)
		{
			return EnumSortingOption.DurationReversed;
		}
		if(sort== EnumSortingOption.DurationReversed)
		{
			return EnumSortingOption.Duration;
		}
		return EnumSortingOption.Duration;
	}
	
	public static String getSortTextPrice(EnumSortingOption sort) 
	{		
		if(sort == EnumSortingOption.Price)
		{
			return "Click to sort by highest price";
		}
		if(sort == EnumSortingOption.PriceReversed)
		{
			return "Click to sort by lowest price";			
		}
		return "Click to sort by lowest price";
	}
	
	public static EnumSortingOption getReverseSortPrice(EnumSortingOption sort)
	{
		if(sort == EnumSortingOption.Price)
		{
			return EnumSortingOption.PriceReversed;
		}
		if(sort== EnumSortingOption.PriceReversed)
		{
			return EnumSortingOption.Price;
		}
		return EnumSortingOption.Price;
	}
	

	public static ItemStack getButtonDisplay(EnumSortingOption type, EnumSortingOption sort)
	{
        ItemStack returnItem = null;
        if(type == EnumSortingOption.AZ)
        {
	        if(sort == EnumSortingOption.AZ || sort == EnumSortingOption.AZReversed)
	        {
	        	returnItem = new ItemStack(Items.CRIMSON_SIGN);
	        	returnItem.enchant(Enchantments.SHARPNESS, 1);
	        	returnItem.hideTooltipPart(TooltipDisplayFlags.ENCHANTMENTS);
	        }
	        else
	        {
	        	returnItem = new ItemStack(Items.CRIMSON_SIGN);	        	
	        }
        }
        if(type == EnumSortingOption.Duration)
        {
	        if(sort == EnumSortingOption.Duration || sort == EnumSortingOption.DurationReversed)
	        {
	        	returnItem = new ItemStack(PixelmonItems.silver_hourglass);
	        	returnItem.enchant(Enchantments.SHARPNESS, 1);
	        	returnItem.hideTooltipPart(TooltipDisplayFlags.ENCHANTMENTS);
	        	returnItem.getTag().putString("tooltip", "");
	        }
	        else
	        {
	        	returnItem = new ItemStack(PixelmonItems.silver_hourglass);
	        	returnItem.getOrCreateTag().putString("tooltip", "");
	        }
        }
        if(type == EnumSortingOption.Price)
        {
	        if(sort == EnumSortingOption.Price || sort == EnumSortingOption.PriceReversed)
	        {
	        	returnItem = new ItemStack(PixelmonItems.amulet_coin);
	        	returnItem.enchant(Enchantments.SHARPNESS, 1);
	        	returnItem.hideTooltipPart(TooltipDisplayFlags.ENCHANTMENTS);
	        	returnItem.getTag().putString("tooltip", "");
	        }
	        else
	        {
	        	returnItem = new ItemStack(PixelmonItems.amulet_coin);
	        	returnItem.getOrCreateTag().putString("tooltip", "");
	        }
        }
        
        return returnItem;
	}
	
	public static ArrayList<ITextComponent> replaceWithHistoryLines(ArrayList<ITextComponent> lstLore, GTSListingHistory listingHistory)
	{
		String buyerName = UsernameCache.getLastKnownUsername(listingHistory.getListingBuyer());

		if(buyerName == null)
		{
			buyerName = "Someone";
		}
		
		lstLore.set(0, new StringTextComponent(TextFormatting.RED + "Buyer: " + TextFormatting.GOLD + buyerName));
		lstLore.set(1, new StringTextComponent(TextFormatting.RED + "Price: " + TextFormatting.GOLD + listingHistory.getListingPrice()));
		lstLore.set(2, new StringTextComponent(TextFormatting.RED + "Sold: " + TextFormatting.GOLD + DurationFormatUtils.formatDuration((System.currentTimeMillis() - listingHistory.getListingEnd()),"dd'd 'HH'h 'mm'm 'ss's'", false) + " ago"));
		return lstLore;
	}
	
	public static ArrayList<ITextComponent> getItemStackLore(ItemStack itemStack)
	{
		CompoundNBT itemNBT = itemStack.getTag();
		CompoundNBT displayNBT = itemNBT.getCompound("display");
		ListNBT loreList = displayNBT.getList("Lore", Constants.NBT.TAG_STRING);
		
		ArrayList<ITextComponent> newLoreList = new ArrayList<ITextComponent>();
		
		for(INBT loreString : loreList)
		{
			newLoreList.add(ITextComponent.Serializer.fromJson(loreString.getAsString()));
		}
		
		return newLoreList;
	}
}
