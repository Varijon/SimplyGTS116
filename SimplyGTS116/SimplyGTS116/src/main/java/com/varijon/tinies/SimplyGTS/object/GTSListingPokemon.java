package com.varijon.tinies.SimplyGTS.object;

import java.util.UUID;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonFactory;
import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.enums.EnumListingType;

import net.minecraft.nbt.JsonToNBT;

public class GTSListingPokemon extends GTSListing
{
	String pokemonNBTData;
	boolean soldAsBreedable;
	transient Pokemon pokemonData;
	
	public GTSListingPokemon(EnumListingType listingType, EnumListingStatus listingStatus, long listingStart,
			long listingEnd, int listingPrice, UUID listingOwner, UUID listingID, String pokemonNBTData, boolean soldAsBreedable) 
	{
		super(listingType, listingStatus, listingStart, listingEnd, listingPrice, listingOwner, listingID, listingID);
		this.pokemonNBTData = pokemonNBTData;
		this.soldAsBreedable = soldAsBreedable;
	}

	public Pokemon createOrGetPokemonData()
	{
		if(pokemonData != null)
		{
			return pokemonData;
		}
		else
		{
			try {
				this.pokemonData = PokemonFactory.create(JsonToNBT.parseTag(pokemonNBTData));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pokemonData;
	}
	
	public String getPokemonNBTData() {
		return pokemonNBTData;
	}

	public void setPokemonNBTData(String pokemonNBTData) {
		this.pokemonNBTData = pokemonNBTData;
	}

//	public Pokemon getPokemonData() {
//		return pokemonData;
//	}

	public void setPokemonData(Pokemon pokemonData) {
		this.pokemonData = pokemonData;
	}

	public boolean isSoldAsBreedable() {
		return soldAsBreedable;
	}

	public void setSoldAsBreedable(boolean soldAsBreedable) {
		this.soldAsBreedable = soldAsBreedable;
	}
	
	
	
}
