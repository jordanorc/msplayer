package br.ufes.inf.lprm.msplayer.image;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;

public class ImageEditor extends JFrame {
	
	public ImageEditor() {
		this(null);
	}
	
	public ImageEditor(BufferedImage image) {
		super();
		
		ImagePanel panel = new ImageEditor.ImagePanel(image);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(panel), BorderLayout.CENTER);
		getContentPane().add(new ImageControlsPanel(panel), BorderLayout.SOUTH);

		setSize(650, 650);
	}
	
	public void centerWindow() {
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
	    this.setLocation(x, y);
	}
	
	public static void main(String[] args) {
		ImageEditor editor = new ImageEditor();

		editor.centerWindow();
		editor.setVisible(true);
	}

	public static class ImagePanel extends JPanel {
		BufferedImage originalImage;
		BufferedImage image;
		double scale;

		public ImagePanel() {
			this(null);
		}
		
		public ImagePanel(BufferedImage image) {
			scale = 1.0;
			setBackground(Color.black);
			try {
					if (image == null) {
				setImage(ImageIO.read(ImageEditor.class.getClassLoader().getResourceAsStream(("icons/image_bg.png"))));
				} else {
					setImage(image);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			int w = getWidth();
			int h = getHeight();
			if (image != null) {
				int imageWidth = image.getWidth();
				int imageHeight = image.getHeight();
				double x = (w - scale * imageWidth) / 2;
				double y = (h - scale * imageHeight) / 2;
				AffineTransform at = AffineTransform.getTranslateInstance(x, y);
				at.scale(scale, scale);
				g2.drawRenderedImage(image, at);
			}
		}

		/**
		 * For the scroll pane.
		 */
		public Dimension getPreferredSize() {
			if (image != null) {
				int w = (int) (scale * image.getWidth());
				int h = (int) (scale * image.getHeight());
				return new Dimension(w, h);
			}
			return super.getPreferredSize();
		}

		public void setScale(double s) {
			scale = s;
			revalidate(); // update the scroll pane
			repaint();
		}
		
		public void zoomIn() {
			setScale(scale + 0.1);
		}
		
		public void zoomOut() {
			setScale(scale - 0.1);
		}

		public void loadImage(BufferedImage image) {
			this.image = image;
			revalidate();
			repaint();
		}

		public BufferedImage fromPath(String path) {
			File f = new File(path);
			try {
				return ImageIO.read(f);

				// image = RGBConverter.to(image, RGB.RED);

			} catch (MalformedURLException mue) {
				System.out.println("URL trouble: " + mue.getMessage());
			} catch (IOException ioe) {
				System.out.println("read trouble: " + ioe.getMessage());
			}
			return null;
		}

		public void loadImage(String path) {
			this.loadImage(fromPath(path));
		}

		public void setImage(String path) {
			this.setImage(fromPath(path));
		}

		public void setImage(BufferedImage image) {
			this.image = image;
			this.originalImage = image;
			revalidate();
			repaint();
		}

		public BufferedImage getImage() {
			return this.originalImage;
		}
		
		public void reset() {
			this.setImage(this.originalImage);
		}
	}

	public static class ImageZoom {
		ImagePanel imagePanel;

		public ImageZoom(ImagePanel ip) {
			imagePanel = ip;
		}

		public JPanel getUIPanel() {
			SpinnerNumberModel model = new SpinnerNumberModel(1.0, 0.1, 10, .01);
			final JSpinner spinner = new JSpinner(model);
			spinner.setPreferredSize(new Dimension(45, spinner.getPreferredSize().height));
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					float scale = ((Double) spinner.getValue()).floatValue();
					imagePanel.setScale(scale);
				}
			});
			JPanel panel = new JPanel();
			panel.add(new JLabel("scale"));
			panel.add(spinner);
			return panel;
		}
	}
}