package net.teamfruit.projectrtm.rtm.render;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.ngtlib.io.NGTLog;
import net.teamfruit.projectrtm.ngtlib.io.ScriptUtil;
import net.teamfruit.projectrtm.ngtlib.math.NGTMath;
import net.teamfruit.projectrtm.ngtlib.renderer.GLHelper;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtil;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtilClient;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetBase;

@SideOnly(Side.CLIENT)
public abstract class PartsRenderer<T, MS extends ModelSetBase> {
	public static final String PACKAGE_NAME = "jp.ngt.rtm.render";
	public static Map<String, Class> rendererMap = new HashMap<String, Class>();
	public static Calendar CALENDAR = Calendar.getInstance();

	protected List<Parts> partsList = new ArrayList<Parts>();
	protected MS modelSet;
	protected ModelObject modelObj;
	protected ScriptEngine script;
	protected Map<Integer, Object> dataMap = new HashMap<Integer, Object>();

	public int currentMatId;

	public PartsRenderer(final String... par1) {
	}

	public Parts registerParts(final Parts par1) {
		this.partsList.add(par1);
		return par1;
	}

	public void init(final MS par1, final ModelObject par2) {
		this.modelSet = par1;
		this.modelObj = par2;

		if (this.script!=null)
			ScriptUtil.doScriptFunction(this.script, "init", par1, par2);

		for (final Parts parts : this.partsList)
			parts.init(this);
	}

	public void preRender(final T t, final boolean smoothing, final boolean culling, final float par3) {
	}

	public void postRender(final T t, final boolean smoothing, final boolean culling, final float par3) {
	}

	/**
	 * @param t Entity or TileEntity
	 * @param pass 0;通常, 1:半透明, 2~4:発光
	 * @param par3
	 */
	public void render(final T t, final int pass, final float par3) {
		ScriptUtil.doScriptFunction(this.script, "render", t, pass, par3);
	}

	public String getModelName() {
		return this.modelSet.getConfig().getName();
	}

	public float sigmoid(final float par1) {
		if (par1==1.0F||par1==0.0F)
			return par1;
		//float f0 = (par1 - 0.5F) * 10.0F;
		//return 1.0F / (1.0F + (float)Math.pow(Math.E, -f0));//sqrtのほうが早い
		final float f0 = (par1-0.5F)*5.0F;
		final float f1 = (float) (f0/Math.sqrt(1.0D+(double) f0*(double) f0));
		return (f1+1.0F)*0.5F;
	}

	/**指定された座標を中心として回転*/
	public void rotate(final float angle, final char axis, final float x, final float y, final float z) {
		GL11.glTranslatef(x, y, z);
		switch (axis) {
			case 'X':
				GL11.glRotatef(angle, 1.0F, 0.0F, 0.0F);
				break;
			case 'Y':
				GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
				break;
			case 'Z':
				GL11.glRotatef(angle, 0.0F, 0.0F, 1.0F);
				break;
		}
		GL11.glTranslatef(-x, -y, -z);
	}

	public int getMCTime() {
		return (int) NGTUtil.getClientWorld().getWorldTime()%24000;
	}

	public int getMCHour() {
		final int t0 = this.getMCTime();
		return (t0/1000+6)%24;
	}

	public int getMCMinute() {
		final int t0 = this.getMCTime();
		return (int) (t0%1000*0.06F);
	}

	public int getSystemTime() {
		return (int) (System.currentTimeMillis()/1000L%86400L);
	}

	/**@return 0~24*/
	public int getSystemHour() {
		return CALENDAR.get(Calendar.HOUR_OF_DAY);
	}

	public int getSystemMinute() {
		return CALENDAR.get(Calendar.MINUTE);
	}

	public Object getData(final int id) {
		if (this.dataMap.containsKey(id))
			return this.dataMap.get(id);
		return 0;
	}

	public void setData(final int id, final Object value) {
		this.dataMap.put(id, value);
	}

	public static Vector3f getViewerVec(final double x, final double y, final double z) {
		final Entity viewer = NGTUtilClient.getMinecraft().renderViewEntity;
		final float vx = (float) (viewer.posX-x);
		final float vy = (float) (viewer.posY+viewer.getEyeHeight()-y);
		final float vz = (float) (viewer.posZ-z);
		return new Vector3f(vx, vy, vz);
	}

	public void renderLightEffect(final Vector3f normal, final double[] pos, final float rL, final float rS, final float length, final int color, final int type, final boolean reverse) {
		GL11.glDisable(GL11.GL_CULL_FACE);

		RenderHelper.disableStandardItemLighting();
		GLHelper.setLightmapMaxBrightness();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDepthMask(false);

		renderLightEffectS(normal, pos[0], pos[1], pos[2], rL, rS, length, color, type, reverse);

		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		RenderHelper.enableStandardItemLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	protected static final double BRIGHTNESS_RATE = 1.0D/256.0D;
	protected static final boolean DEBUG = false;
	protected static final byte DIV_NUM = 32;
	protected static final float ANGLE = 360.0F/DIV_NUM;

	@SuppressWarnings("unused")
	public static void renderLightEffectS(final Vector3f normal, final double x, final double y, final double z, final float rL, final float rS, final float length, final int color, final int type, final boolean reverse) {
		final boolean useVec = normal!=null;
		Vector3f viewerVec = null;
		float viewerAngle = 0.0F;//2ベクトルのなす角
		if (useVec) {
			viewerVec = getViewerVec(x, y, z);
			viewerAngle = NGTMath.toDegrees(Vector3f.angle(normal, viewerVec));
		}

		if (reverse)
			viewerAngle = MathHelper.wrapAngleTo180_float(viewerAngle+180.0F);

		if (viewerAngle>90.0F)
			viewerAngle = 180.0F-viewerAngle;//裏側

		float lightStrength = 1.0F;//Viewerが正面にいるとき1.0
		if (viewerAngle>45.0F)
			lightStrength = (90.0F-viewerAngle)/45.0F;

		final Tessellator tessellator = Tessellator.instance;

		if (DEBUG&&useVec) {
			//NGTLog.debug("%7.3f,%7.3f,%7.3f", normal.x, normal.y, normal.z);
			tessellator.startDrawing(GL11.GL_LINES);
			tessellator.setColorRGBA_I(0xFF0000, 0xFF);
			tessellator.addVertex(0.0D, 0.0D, 0.0D);
			tessellator.addVertex(normal.x, normal.y, normal.z);
			tessellator.setColorRGBA_I(0x00FF00, 0xFF);
			tessellator.addVertex(0.0D, 0.0D, 0.0D);
			tessellator.addVertex(viewerVec.x, viewerVec.y, viewerVec.z);
			tessellator.draw();
		}

		if (type==0)//フレア
		{
			tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
			tessellator.setColorRGBA_I(color, 0xFF);
			tessellator.addVertex(0.0D, 0.0D, 0.0D);
			tessellator.setColorRGBA_I(0x000000, 0x00);
			for (int i = 0; i<=DIV_NUM; ++i) {
				final float rad = NGTMath.toRadians(i*ANGLE);
				tessellator.addVertex(MathHelper.cos(rad)*rL*lightStrength, MathHelper.sin(rad)*rL*lightStrength, 0.0D);
			}
			tessellator.draw();
		} else if (type==1)//ボリュームライト
		{
			final float angle = NGTMath.toDegrees((float) Math.atan2(rL, length));
			float distance = 256.0F;
			if (viewerVec!=null)
				distance = viewerVec.lengthSquared();
			float brightness = 0.0F;
			if (viewerAngle<angle)
				brightness = 1.0F-viewerAngle/angle;
			else// if(viewerAngle > 45.0F && distance > 64.0F)//256
			{
				/*float b0 = ((viewerAngle - 45.0F) * 0.0222222F);
				float b1 = (float)((double)(distance - 64.0F) * 0.015625D);//0.00390625
				if(b1 > 1.0F)
				{
					b1 = 1.0F;
				}
				brightness = b0 * b1;*/
				final float b0 = (viewerAngle-angle)/(90.0F-angle);
				float b1 = (float) (distance*BRIGHTNESS_RATE);
				if (b1>1.0F)
					b1 = 1.0F;
				brightness = b0*b1;
			}

			if (brightness>0.0F) {
				final int alpha = (int) (255.0F*brightness);
				//float f2 = (f0 / 90.0F);//f0:viewAngRad
				tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
				tessellator.setColorRGBA_I(color, alpha);
				//tessellator.addVertex(0.0D, 0.0D, length * f2);
				tessellator.addVertex(0.0D, 0.0D, 0.0D);
				tessellator.setColorRGBA_I(0x000000, 0x00);
				for (int i = 0; i<=DIV_NUM; ++i) {
					final float rad = NGTMath.toRadians(-i*ANGLE);
					tessellator.addVertex(MathHelper.cos(rad)*rL, MathHelper.sin(rad)*rL, length);
				}
				tessellator.draw();

				float b3 = (float) (distance*BRIGHTNESS_RATE);
				if (b3>1.0F)
					b3 = 1.0F;
				//float f3 = 0.0625F * b3;
				final float f3 = rS*b3;

				tessellator.startDrawing(GL11.GL_TRIANGLES);
				for (int i = 0; i<=DIV_NUM; ++i) {
					final float rad = NGTMath.toRadians(i*ANGLE);
					//tessellator.setColorRGBA_I(0x00FF00, 0xFF);
					tessellator.setColorRGBA_I(0x000000, 0x00);
					tessellator.addVertex(MathHelper.cos(rad)*rL, MathHelper.sin(rad)*rL, length);
					tessellator.setColorRGBA_I(color, alpha>>1);//0x80
					tessellator.addVertex(0.0D, 0.0D, 0.0D);
					//tessellator.setColorRGBA_I(0xFF0000, 0xFF);
					tessellator.setColorRGBA_I(0x000000, 0x00);
					tessellator.addVertex(MathHelper.cos(rad)*f3, MathHelper.sin(rad)*f3, 0.0D);
				}
				tessellator.draw();
			}
		}
	}

	public void bindTexture(final ResourceLocation texture) {
		NGTUtilClient.bindTexture(texture);
	}

	public abstract World getWorld(T entity);

	/*---------------------------------------------------------------------------------------------------------------------------------------*/

	public static <R extends PartsRenderer> R getRendererWithScript(final ResourceLocation par1, final String... args) throws ReflectiveOperationException {
		final ScriptEngine se = ScriptUtil.doScript(par1);
		final String s = (String) ScriptUtil.getScriptField(se, "renderClass");
		final Class clazz = Launch.classLoader.loadClass(s);
		final Constructor<R> constructor = clazz.getConstructor(String[].class);
		final R renderer = constructor.newInstance(new Object[] { args });
		renderer.script = se;
		se.put("renderer", renderer);
		return renderer;
	}

	//Java形式描画スクリプト(廃止)
	/*@Deprecated
	public static <R extends PartsRenderer> R getRenderer(ResourceLocation par1) throws ReflectiveOperationException, IOException
	{
		String path = par1.getResourcePath();
		String code = NGTText.getText(par1, true);//改行必須
		String className = path.substring(path.lastIndexOf("/") + 1, path.indexOf(".jsrc"));
		String qualifiedName = PACKAGE_NAME + "." + className;
		R renderer = getInstance(qualifiedName, code);
		if(renderer != null)
		{
			NGTLog.debug("Create Renderer : " + qualifiedName);
			return renderer;
		}
		return null;
	}*/

	/*@Deprecated
	public static <C> C getInstance(String name, String source) throws ReflectiveOperationException
	{
		Class<C> clazz;
		if(rendererMap.containsKey(name))
		{
			clazz = rendererMap.get(name);
		}
		else
		{
			clazz = NGTClassUtil.compile(name, source);
			if(clazz == null)
			{
				return null;
			}
			rendererMap.put(name, clazz);
		}
	    return clazz.newInstance();//同じパッケージ内で呼ぶ必要あり
	}*/

	public void debug(final String msg, final Object... args) {
		NGTLog.debug(msg, args);
	}

	public static boolean validPath(final String par1) {
		return par1!=null&&par1.length()>0;
	}
}