package io.keikai.devref;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.A;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;

/**
 * Scan each folders and generate links to every page.
 * @author Hawk
 * 
 */
public class IndexComposer extends SelectorComposer<Component> {

	@Wire
	private Vlayout linkArea;
	private Path webRootPath = Paths.get(WebApps.getCurrent().getRealPath(""));

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		List<File> folders = ExampleFileWalker.scanFolders(webRootPath);

		for (File folder : folders){
	    	File[] files = folder.listFiles(new PageFilter());
	    	appendPageLinks(webRootPath.relativize(folder.toPath()).toString(), files);
	    }
	}

	private void appendPageLinks(String folderPath, File[] files) {
		renderFolderAsTitle(folderPath);
		renderPageLinks(folderPath, files);
	}

	private void renderPageLinks(String folderPath, File[] files) {
		List<File> fileList = Arrays.asList(files);
		Collections.sort(fileList);
		for (File zulFile : fileList){
			A link = new A(zulFile.getName());
			link.setHref(folderPath + File.separator + zulFile.getName());
			linkArea.appendChild(link);
		}
	}

	private void renderFolderAsTitle(String folderPath) {
		Label title = new Label(folderPath);
		title.setSclass("title");
		linkArea.appendChild(title);
	}

	/**
	 * accept .zul, .jsp, .xhtml
	 */
	class PageFilter implements FileFilter{

		@Override
		public boolean accept(File pathname) {
			return pathname.isFile()
					&& (pathname.getName().toLowerCase().endsWith("zul")
						|| pathname.getName().toLowerCase().endsWith("jsp")
						|| pathname.getName().endsWith("xhtml"))
					&& !pathname.getName().equals("index.zul");
		}
		
	}
}
