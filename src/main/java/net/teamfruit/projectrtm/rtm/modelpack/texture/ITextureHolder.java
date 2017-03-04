package net.teamfruit.projectrtm.rtm.modelpack.texture;

import net.teamfruit.projectrtm.rtm.modelpack.texture.TextureManager.TexturePropertyType;

public interface ITextureHolder<T> {
	T getProperty();

	void setTexture(String name);

	TexturePropertyType getType();
}