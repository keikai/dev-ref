package io.keikai.devref;

import java.io.File;
import java.io.FileFilter;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.A;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;

/**
 * Generate links to every page.
 * @author Hawk
 * 
 */
@SuppressWarnings("serial")
public class IndexComposer extends SelectorComposer<Component> {

	@Wire
	private Vlayout linkArea;
	static private String INDEX_ZUL = "index.zul";
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		generateLinksOfZul();
	}

	private void generateLinksOfZul() {
		String webRootRealPath = WebApps.getCurrent().getRealPath("/");
		File webRoot = new File(webRootRealPath);
	    // list the files using our FileFilter
	    File[] files = webRoot.listFiles(new ZulFileFilter());
	    appendPageLinks("/", files);
	    //scan folders
	    File[] folders = webRoot.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() && (!pathname.getName().equals("WEB-INF") && !pathname.getName().equals("js"));
			}
		});
	    for (File folder : folders){
	    	files = folder.listFiles(new ZulFileFilter());
	    	appendPageLinks(folder.getPath().substring(webRootRealPath.length()) + File.separator, files);
	    }
	}
	
	private void appendPageLinks(String folderPath, File[] files) {
		if (!folderPath.equals("/")){
			Label title = new Label(folderPath);
			title.setSclass("title");
			linkArea.appendChild(title);
		}
		for (File zulFile : files){
			A link = new A(zulFile.getName());
			link.setHref(folderPath + zulFile.getName());
			linkArea.appendChild(link);
		}
	}

	class ZulFileFilter implements FileFilter{

		@Override
		public boolean accept(File pathname) {
			return (pathname.getName().toLowerCase().endsWith("zul") || pathname.getName().toLowerCase().endsWith("jsp"))
					&& !pathname.getName().equals(INDEX_ZUL);
		}
		
	}
}
