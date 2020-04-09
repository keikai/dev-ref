package io.keikai.devref;

import java.io.File;
import java.io.FileFilter;
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
	static private String INDEX_ZUL = "index.zul";
	String webRootRealPath = WebApps.getCurrent().getRealPath("");

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		List<File> folders = scanFolders();

		for (File folder : folders){
	    	File[] files = folder.listFiles(new PageFilter());
	    	appendPageLinks(folder.getPath().substring(webRootRealPath.length()), files);
	    }
	}

	private List<File>  scanFolders() {
		List<File> folders = new LinkedList<>();
		File webRoot = new File(webRootRealPath);
		folders.add(webRoot);
		folders.addAll(Arrays.asList(webRoot.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() && (!pathname.getName().equals("WEB-INF") && !pathname.getName().equals("js"));
			}
		})));
		Collections.sort(folders);
		return folders;
	}

	private void appendPageLinks(String folderPath, File[] files) {
		if (!folderPath.equals("/")){
			Label title = new Label(folderPath);
			title.setSclass("title");
			linkArea.appendChild(title);
		}
		List<File> fileList = Arrays.asList(files);
		Collections.sort(fileList);
		for (File zulFile : fileList){
			A link = new A(zulFile.getName());
			link.setHref(folderPath + zulFile.getName());
			linkArea.appendChild(link);
		}
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
					&& !pathname.getName().equals(INDEX_ZUL);
		}
		
	}
}
