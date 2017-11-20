package com.teamwizardry.wizardry.client.gui.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.block.IStructure;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;

import static net.minecraft.client.gui.FontRenderer.getFormatFromString;

public class ComponentContentPage extends GuiComponent {

	@NotNull
	private final BookGui bookGui;
	private final JsonObject resource;
	private GuiComponent prevComps;

	public ComponentContentPage(@NotNull BookGui bookGui, int posX, int posY, int width, int height, JsonObject resource) {
		super(posX, posY, width, height);
		this.bookGui = bookGui;
		this.resource = resource;

		HashMap<Integer, GuiComponent> pages = getContent();

		ComponentNavBar navBar = new ComponentNavBar((int) (getSize().getX() / 2.0 - 170 / 2.0), getSize().getYi() + 30, 170, 15, pages.size() - 1);
		add(navBar);

		navBar.BUS.hook(EventNavBarChange.class, eventNavBarChange -> {
			if (prevComps != null) {
				GuiComponent parent = prevComps.getParent();
				if (parent != null) parent.relationships.remove(prevComps);
			}

			if (pages.size() <= navBar.getPage()) return;

			GuiComponent content = pages.get(navBar.getPage());
			add(content);
			prevComps = content;
		});
		navBar.BUS.fire(new EventNavBarChange());
	}

	private HashMap<Integer, GuiComponent> getContent() {
		HashMap<Integer, GuiComponent> pages = new HashMap<>();

		JsonObject object = resource;
		if (object.has("title") && object.has("type") && object.get("title").isJsonPrimitive() && object.get("type").isJsonPrimitive()) {

			// TODO: String title = object.get("title").getAsString();
			if (object.get("type").getAsString().equals("content")) {


				if (object.has("content") && object.get("content").isJsonArray()) {
					JsonArray array = object.get("content").getAsJsonArray();

					int i = 0;
					for (JsonElement content : array) {
						if (!content.isJsonObject()) continue;

						JsonObject contObj = content.getAsJsonObject();
						if (contObj.has("text") && contObj.get("text").isJsonArray()) {

							for (JsonElement lineElement : contObj.get("text").getAsJsonArray()) {
								if (!lineElement.isJsonPrimitive()) continue;

								HashSet<GuiComponent> componentsAfterThisPage = new HashSet<>();
								String s = lineElement.getAsString();
								String[] images = StringUtils.substringsBetween(s, "[image:", "]");
								String[] structures = StringUtils.substringsBetween(s, "[structure:", "]");
								if (images != null)
									for (String image : images) {
										ResourceLocation location = new ResourceLocation(Wizardry.MODID, "textures/bookimages/" + image + ".png");
										Sprite sprite = new Sprite(location);

										ComponentSprite componentSprite = new ComponentSprite(sprite, 0, 45, 200, 200);
										ComponentSprite lineBreak1 = new ComponentSprite(BookGui.LINE_BREAK, (int) (getSize().getX() / 2.0 - 177.0 / 2.0), -5, 177, 2);
										ComponentSprite lineBreak2 = new ComponentSprite(BookGui.LINE_BREAK, (int) (getSize().getX() / 2.0 - 177.0 / 2.0), 203, 177, 2);
										componentSprite.add(lineBreak1, lineBreak2);

										componentsAfterThisPage.add(componentSprite);
										s = s.replace("[image:" + image + "]", "");
									}

								if (structures != null)
									for (String structure : structures) {
										ResourceLocation location = new ResourceLocation(Wizardry.MODID, structure);
										Block block = ForgeRegistries.BLOCKS.getValue(location);
										if (block == null || !(block instanceof IStructure)) continue;

										ComponentVoid plate = new ComponentVoid(0, 0, getSize().getXi(), getSize().getYi());
										ComponentStructure componentStructure = new ComponentStructure(bookGui, 0, 0, getSize().getXi(), getSize().getYi(), (IStructure) block);
										ComponentSprite lineBreak1 = new ComponentSprite(BookGui.LINE_BREAK, (int) (getSize().getX() / 2.0 - 177.0 / 2.0), -5, 177, 2);
										ComponentSprite lineBreak2 = new ComponentSprite(BookGui.LINE_BREAK, (int) (getSize().getX() / 2.0 - 177.0 / 2.0), getSize().getYi() - 5, 177, 2);
										plate.add(componentStructure, lineBreak1, lineBreak2);

										componentsAfterThisPage.add(plate);
										s = s.replace("[structure:" + structure + "]", "");
									}

								// split each line in json into pages if need be.
								String chunks = wrapFormattedStringToWidth(s, 1850);

								for (String chunk : chunks.split("/L")) {
									ComponentText page = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
									page.getTransform().setScale(2);
									page.getUnicode().setValue(true);
									page.getWrap().setValue(100);
									page.getText().setValue(chunk);
									pages.put(i++, page);

									ComponentSprite lineBreak1 = new ComponentSprite(BookGui.LINE_BREAK, (int) (getSize().getX() / 2.0 - 177.0 / 2.0), -5, 177, 2);
									ComponentSprite lineBreak2 = new ComponentSprite(BookGui.LINE_BREAK, (int) (getSize().getX() / 2.0 - 177.0 / 2.0), getSize().getYi() - 5, 177, 2);
									page.add(lineBreak1, lineBreak2);
								}

								for (GuiComponent component : componentsAfterThisPage) {
									pages.put(i++, component);
								}
							}
						}
					}
				}
			}
		}
		return pages;
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {

	}

	private int sizeStringToWidth(String str, int wrapWidth) {
		int i = str.length();
		int j = 0;
		int k = 0;
		int l = -1;

		for (boolean flag = false; k < i; ++k) {
			String c0 = str.charAt(k) + "";

			switch (c0) {
				case "/L":
					--k;
					break;
				case " ":
					l = k;
				default:
					j += Minecraft.getMinecraft().fontRenderer.getStringWidth(c0);

					if (flag) {
						++j;
					}

					break;
				case "\u00a7":

					if (k < i - 1) {
						++k;
						char c1 = str.charAt(k);

						if (c1 != 'l' && c1 != 'L') {
							if (c1 == 'r' || c1 == 'R' || isFormatColor(c1)) {
								flag = false;
							}
						} else {
							flag = true;
						}
					}
			}

			if (c0.equals("/L")) {
				++k;
				l = k;
				break;
			}

			if (j > wrapWidth) {
				break;
			}
		}

		return k != i && l != -1 && l < k ? l : k;
	}

	private boolean isFormatColor(char colorChar) {
		return colorChar >= '0' && colorChar <= '9' || colorChar >= 'a' && colorChar <= 'f' || colorChar >= 'A' && colorChar <= 'F';
	}

	private String wrapFormattedStringToWidth(String str, int wrapWidth) {
		int i = sizeStringToWidth(str, wrapWidth);

		if (str.length() <= i) {
			return str;
		} else {
			String s = str.substring(0, i);
			String c0 = str.charAt(i) + "";
			boolean flag = c0.equals(" ") || c0.equals("/L");
			String s1 = getFormatFromString(s) + str.substring(i + (flag ? 1 : 0));
			return s + "/L" + this.wrapFormattedStringToWidth(s1, wrapWidth);
		}
	}
}
