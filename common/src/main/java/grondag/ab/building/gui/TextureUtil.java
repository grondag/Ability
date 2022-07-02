package grondag.ab.building.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;

import grondag.ab.ux.client.ScreenRenderContext;
import grondag.xm.api.texture.TextureSet;

public class TextureUtil {
	public static BufferBuilder setupRendering(ScreenRenderContext renderContext) {
		renderContext.minecraft().getTextureManager().bindForSetup(InventoryMenu.BLOCK_ATLAS);
		renderContext.minecraft().getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
		RenderSystem.enableTexture();
		RenderSystem.enableBlend();
		//RenderSystem.disableAlphaTest();
		RenderSystem.defaultBlendFunc();
		//RenderSystem.shadeModel(GL21.GL_SMOOTH);
		//RenderSystem.color4f(1, 1, 1, 1);

		final Tesselator tessellator = Tesselator.getInstance();
		final BufferBuilder vertexbuffer = tessellator.getBuilder();

		vertexbuffer.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

		return vertexbuffer;
	}

	public static void tearDownRendering() {
		Tesselator.getInstance().end();
		//RenderSystem.color4f(1, 1, 1, 1);
		//RenderSystem.shadeModel(GL21.GL_FLAT);
		RenderSystem.disableBlend();
		//RenderSystem.enableAlphaTest();
		RenderSystem.disableTexture();
	}

	public static void bufferTexture(BufferBuilder vertexBuffer, double left, double top, int itemSize, int color, TextureSet item) {
		final double bottom = top + itemSize;
		final double right = left + itemSize;
		final TextureAtlasSprite sprite = item.sampleSprite();

		final float u0 = sprite.getU0();
		final float u1 = u0 + (sprite.getU1() - u0) / item.scale().sliceCount;

		final float v0 = sprite.getV0();
		final float v1 = v0 + (sprite.getV1() - v0) / item.scale().sliceCount;
		final int a = (color >> 24) & 0xFF;
		final int r = (color >> 16) & 0xFF;
		final int g = (color >> 8) & 0xFF;
		final int b = color & 0xFF;

		vertexBuffer.vertex(left, bottom, 100.0D).uv(u0, v1).color(r, g, b, a).endVertex();
		vertexBuffer.vertex(right, bottom, 100.0D).uv(u1, v1).color(r, g, b, a).endVertex();
		vertexBuffer.vertex(right, top, 100.0D).uv(u1, v0).color(r, g, b, a).endVertex();
		vertexBuffer.vertex(left, top, 100.0D).uv(u0, v0).color(r, g, b, a).endVertex();
	}
}
