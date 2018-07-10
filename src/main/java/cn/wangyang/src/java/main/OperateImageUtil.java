package cn.wangyang.src.java.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 图片处理
 * 
 * @author alao
 */
public final class OperateImageUtil {

	public static final String IMG_FORMAT = "jpg";

	/** 二维码设定值 */
	public static final int QR_IMAGE_WIDTH = 135;// 图片百分比

	public static final int QR_IMAGE_HEIGHT = 135;// 图片百分比

	public static final int QR_IMAGE_X = 325;// 图片x轴距离

	public static final int QR_IMAGE_Y = 640;// 图片y轴距离

	/** 昵称内容设定值 */
	public static final int TEXT_X = 110;// 昵称x轴距离

	public static final int TEXT_Y = 60;// 昵称y轴距离

	/** 头像设定值 */
	public static final int PHOTO_WIDTH = 70;// 头像百分比

	public static final int PHOTO_HEIGHT = 70;// 头像百分比

	public static final int PHOTO_X = 25;// 头像x轴距离

	public static final int PHOTO_Y = 23;// 头像y轴距离

	public static final String GENERATE_QRCODE_IMAGE_NAME = "_qrcode.jpg";// 二维码内容

	public static final String GENERATE_PHOTO_IMAGE_NAME = "_photo.jpg";// 头像地址

	public static final String GENERATE_RESULT_IMAGE_NAME = "_result.jpg";// 最终结果图片名称

	public static void main(String[] args) {
		// 图片模版目录
		String imagePath = "/wangyang/var/";
		// 图片模版名称
		String tempImageName = "Demo.jpg";
		// 我滴头像像😁
		String photoUrl = "http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTK06ibPgUpZ0zibUN6OE04x4xwwFNsJu6ePJNv9TFlHeib9L6ghvqFoRMyOB5iap6xdDTqMw7djNbpZ8g/132";
		// 二维码内容，可根据需要生成符合业务逻辑的二维码.我这里填的是我的github地址。谢谢老铁followers一波～～～
		String qrContents = "https://github.com/WangSleep";
		// 昵称啦。注意，这里不支持表情。比如😄，🙄️之类。表情的话，解析不出来。
		String nickname = "alao";
		// 最终生成图片名称啦。和GENERATE_RESULT_IMAGE_NAME拼接起来。可根据业务需求，保持唯一还是保留所有。
		String generateImageKey = "333";
		// 这里是生成图片的方法啦。
		generateImageName(imagePath, tempImageName, photoUrl, qrContents, nickname, generateImageKey);
	}

	/**
	 * 图片合成处理
	 * 
	 * @param imagePath
	 *            文件本地存储地址
	 * @param tempImageName
	 *            模版文件名称
	 * @param photoUrl
	 *            头像URL地址
	 * @param qrContents
	 *            qr内容
	 * @param nickname
	 *            用户昵称
	 * @param generateImageKey
	 *            唯一识别
	 * @return 生成文件名称
	 * 
	 *         功能描述：通过Graphics类来勾画图片，拿其中一个图片作为背景，其他图片darw上去。其中包含，读取微信头像，生成二维码，头像压缩，圆形化，文字设定等一系列处理。
	 * 
	 *         图片格式不限制jpg，可根据业务需求变更。
	 * 
	 */
	public static String generateImageName(String imagePath, String tempImageName, String photoUrl, String qrContents,
			String nickname, String generateImageKey) {
		String resultName = null;
		ImageBean imageBean = null;
		try {
			// 需要图片合成对象
			List<ImageBean> imagePathList = new ArrayList<ImageBean>();
			// 需要文字合成对象
			List<TextBean> contentList = new ArrayList<TextBean>();

			// 生成二维码
			if (StringUtils.isNotBlank(qrContents)) {
				drawLogoQRCode(imagePath + generateImageKey + GENERATE_QRCODE_IMAGE_NAME, qrContents);
				// 二维码
				imageBean = new ImageBean();
				imageBean.setImagePath(imagePath + generateImageKey + GENERATE_QRCODE_IMAGE_NAME);
				imageBean.setImageX(QR_IMAGE_X);
				imageBean.setImageY(QR_IMAGE_Y);
				imagePathList.add(imageBean);
			}
			// 头像
			if (StringUtils.isNotBlank(photoUrl)) {
				photo(photoUrl, imagePath + generateImageKey + GENERATE_PHOTO_IMAGE_NAME);
				imageBean = new ImageBean();
				imageBean.setImagePath(imagePath + generateImageKey + GENERATE_PHOTO_IMAGE_NAME);
				imageBean.setImageX(PHOTO_X);
				imageBean.setImageY(PHOTO_Y);
				imagePathList.add(imageBean);
			}
			// 昵称
			if (StringUtils.isNotBlank(nickname)) {
				TextBean textBean = new TextBean();
				textBean.setTextContent(nickname);
				textBean.setTextX(TEXT_X);
				textBean.setTextY(TEXT_Y);
				contentList.add(textBean);
			}
			// 图片合成处理
			mergeBothImage(imagePath + tempImageName, imagePathList, contentList,
					imagePath + generateImageKey + GENERATE_RESULT_IMAGE_NAME);

			// 生成文件名称返回
			resultName = generateImageKey + GENERATE_RESULT_IMAGE_NAME;

			// QR文件删除
			File qrFile = new File(imagePath + generateImageKey + GENERATE_QRCODE_IMAGE_NAME);
			if (qrFile.exists()) {
				qrFile.delete();
			}
			// 头像文件删除
			File photoFile = new File(imagePath + generateImageKey + GENERATE_PHOTO_IMAGE_NAME);
			if (photoFile.exists()) {
				photoFile.delete();
			}

		} catch (IOException e) {
			// Exception logger输出
		} catch (Exception e) {
			// Exception logger输出
		}
		return resultName;
	}

	/**
	 * 生成二维码
	 * 
	 * @param generateQrPath
	 *            生成二维码存放位置
	 * @param contents
	 *            内容
	 * @throws Exception
	 */
	public static void drawLogoQRCode(String generateQrPath, String contents) throws Exception {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
		hints.put(EncodeHintType.MARGIN, 2);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, QR_IMAGE_WIDTH,
				QR_IMAGE_HEIGHT, hints);
		Path file = new File(generateQrPath).toPath(); // 生成二维码图片
		MatrixToImageWriter.writeToPath(bitMatrix, IMG_FORMAT, file);
	}

	/**
	 * 头像圆形处理
	 * 
	 * @param photoUrl
	 *            头像网络地址
	 * @param photoPath
	 *            头像生成地址
	 */
	public static void photo(String photoUrl, String photoPath) throws Exception {
		BufferedImage url = getUrlByBufferedImage(photoUrl);
		// 压缩小图
		BufferedImage convertImage = scaleByPercentage(url, PHOTO_WIDTH, PHOTO_HEIGHT);
		// 裁剪成圆形
		convertImage = convertCircular(convertImage);
		ImageIO.write(convertImage, photoPath.substring(photoPath.lastIndexOf(".") + 1), new File(photoPath));
	}

	public static void mergeBothImage(String tempImagePath, List<ImageBean> imagePathList, List<TextBean> contentList,
			String generateImagePath) throws IOException {
		InputStream is = null;
		InputStream is2 = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(tempImagePath);
			BufferedImage image = ImageIO.read(is);
			Graphics g = image.getGraphics();
			if (imagePathList != null) {
				for (ImageBean imageBean : imagePathList) {
					is2 = new FileInputStream(imageBean.getImagePath());
					BufferedImage image2 = ImageIO.read(is2);
					g.drawImage(image2, imageBean.getImageX(), imageBean.getImageY(), null);
				}
			}

			if (contentList != null) {
				Font f = new Font("黑体", Font.PLAIN, 20);
				// Color mycolor = Color.red;
				Color mycolor = new Color(78, 84, 158);
				g.setColor(mycolor);
				g.setFont(f);
				for (TextBean textBean : contentList) {
					g.drawString(textBean.getTextContent(), textBean.getTextX(), textBean.getTextY());
				}
			}
			g.dispose();
			os = new FileOutputStream(generateImagePath);
			ImageIO.write(image, IMG_FORMAT, os);
		} catch (Exception e) {
			// Exception logger输出
		} finally {
			if (os != null) {
				os.close();
			}
			if (is2 != null) {
				is2.close();
			}
			if (is != null) {
				is.close();
			}
		}
	}

	/**
	 * 读取头像信息
	 * 
	 * @param url
	 * @return
	 */
	private static BufferedImage getUrlByBufferedImage(String url) {
		try {
			URL urlObj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
			// 连接超时
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setConnectTimeout(25000);
			// 读取超时 --服务器响应比较慢,增大时间
			conn.setReadTimeout(25000);
			conn.setRequestMethod("GET");
			conn.addRequestProperty("Accept-Language", "zh-cn");
			conn.addRequestProperty("Content-type", "image/jpeg");
			conn.addRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727)");
			conn.connect();
			BufferedImage bufImg = ImageIO.read(conn.getInputStream());
			conn.disconnect();
			return bufImg;
		} catch (MalformedURLException e) {
			// Exception logger输出
		} catch (ProtocolException e) {
			// Exception logger输出
		} catch (IOException e) {
			// Exception logger输出
		}
		return null;
	}

	/**
	 * 头像压缩处理
	 * 
	 * @param inputImage
	 * @param newWidth
	 * @param newHeight
	 * @return
	 * @throws Exception
	 */
	private static BufferedImage scaleByPercentage(BufferedImage inputImage, int newWidth, int newHeight)
			throws Exception {
		// 获取原始图像透明度类型
		int type = inputImage.getColorModel().getTransparency();
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		// 开启抗锯齿
		RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// 使用高质量压缩
		renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		BufferedImage img = new BufferedImage(newWidth, newHeight, type);
		Graphics2D graphics2d = img.createGraphics();
		graphics2d.setRenderingHints(renderingHints);
		graphics2d.drawImage(inputImage, 0, 0, newWidth, newHeight, 0, 0, width, height, null);
		graphics2d.dispose();
		return img;
	}

	/**
	 * 头像圆形处理
	 * 
	 * @param bi1
	 * @return
	 * @throws IOException
	 */
	private static BufferedImage convertCircular(BufferedImage bi1) {
		// 透明底的图片
		BufferedImage bi2 = new BufferedImage(bi1.getWidth(), bi1.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, bi1.getWidth(), bi1.getHeight());
		Graphics2D g2 = bi2.createGraphics();
		g2.setClip(shape);
		g2.drawImage(bi1, 0, 0, null);
		// 设置颜色
		g2.setBackground(Color.green);
		g2.dispose();
		return bi2;
	}

	public static class ImageBean {

		private String imagePath;

		private int imageX;

		private int imageY;

		public String getImagePath() {
			return imagePath;
		}

		public void setImagePath(String imagePath) {
			this.imagePath = imagePath;
		}

		public int getImageX() {
			return imageX;
		}

		public void setImageX(int imageX) {
			this.imageX = imageX;
		}

		public int getImageY() {
			return imageY;
		}

		public void setImageY(int imageY) {
			this.imageY = imageY;
		}

	}

	public static class TextBean {

		private String textContent;

		private int textX;

		private int textY;

		public String getTextContent() {
			return textContent;
		}

		public void setTextContent(String textContent) {
			this.textContent = textContent;
		}

		public int getTextX() {
			return textX;
		}

		public void setTextX(int textX) {
			this.textX = textX;
		}

		public int getTextY() {
			return textY;
		}

		public void setTextY(int textY) {
			this.textY = textY;
		}

	}

}
