package ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import deng.music.RenameTools;

public class RenameWindow extends Frame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7655697977240805669L;

	private List<File> fileList = new ArrayList<File>();;

	public RenameWindow() {
		super();
		init();
	}

	private void init() {
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		this.setTitle("Audio Rename Tool");
		this.setSize(400, 300);
		this.setLocationRelativeTo(null);
		this.setLayout(new BorderLayout(0, 0));

		MenuBar menubar = new MenuBar();
		Menu menuAbout = new Menu("about");
		menubar.add(menuAbout);
		setMenuBar(menubar);
		MenuItem itemAbout = new MenuItem("about");
		menuAbout.add(itemAbout);
		itemAbout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JOptionPane.showMessageDialog(null, "Author : 达克·斯盖\nhttp://weibo.com/darksky9321");
			}
		});

		TextArea textPath = new TextArea();
		textPath.setEditable(false);
		this.add("North", textPath);
		new DropTarget(textPath, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
			@Override
			public void drop(DropTargetDropEvent dtde) {
				try {
					// 如果拖入的文件格式受支持
					if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						// 接收拖拽来的数据
						dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
						@SuppressWarnings("unchecked")
						List<File> list = (List<File>) (dtde.getTransferable()
								.getTransferData(DataFlavor.javaFileListFlavor));
						textPath.setText("");
						for (File file : list) {
							textPath.append(file.getAbsolutePath());
							textPath.append("\r\n");
						}
						fileList = list;
						// 指示拖拽操作已完成
						dtde.dropComplete(true);
					} else {
						// 拒绝拖拽来的数据
						dtde.rejectDrop();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		Panel southPanel = new Panel();
		southPanel.setLayout(new GridLayout(2, 1));
		TextField textStr = new TextField("@track2@.@title@ - @artist@");
		Panel panelButton = new Panel();
		southPanel.add(textStr);
		southPanel.add(panelButton);
		panelButton.setLayout(new GridLayout(1, 2));
		Button btn1 = new Button("Rename");
		Button btn2 = new Button("Recursion");
		panelButton.add(btn1);
		panelButton.add(btn2);
		this.add("Center", southPanel);

		btn1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				textPath.append("Rename...\n");
				for (File f : fileList) {
					if (f.isFile()) {
						RenameTools.renameFile(f, textStr.getText());
					} else if (f.isDirectory()) {
						RenameTools.renameFolder(f, textStr.getText());
					}
				}
				textPath.append("Rename Complete.");
			}
		});

		btn2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				textPath.append("Rename...\n");
				for (File f : fileList) {
					if (f.isFile()) {
						RenameTools.renameFile(f, textStr.getText());
					} else if (f.isDirectory()) {
						RenameTools.renameRecursion(f, textStr.getText());
					}
				}
				textPath.append("Rename Complete.");
			}
		});

		this.setVisible(true);
	}
}
