package com.domochevsky.quiverbow.models;

//TODO Remove this for classloading reasons
//Used for items/blocks that do not have a standard MRL or use CustomMeshDefinitions, etc.
@Deprecated
public interface ISpecialRender
{
	public void registerRender();
}
