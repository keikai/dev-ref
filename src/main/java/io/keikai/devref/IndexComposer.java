package io.keikai.devref;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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
			String folderPath = webRootPath.relativize(folder.toPath()).toString();
			renderFolderAsTitle(folderPath);
			renderPageLinks(folderPath, files);
		}
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
	 * define those files to show by their extension
	 */
	class PageFilter implements FileFilter{

		@Override
		public boolean accept(File pathname) {
			return pathname.isFile()
					&& (pathname.getName().toLowerCase().endsWith("zul")
						|| pathname.getName().toLowerCase().endsWith("jsp")
						|| pathname.getName().endsWith("xhtml")
						|| pathname.getName().endsWith("html"))
					&& !pathname.getName().equals("index.zul");
		}
		
	}

	public static class ExampleFileWalker {

		/**
		 * scan directories from basePath recursively excluding some irrelevant directories.
		 * @return a list of directories that contain example pages
		 */
		static public List<File> scanFolders(Path basePath) throws IOException {
			String[] dir2Skip = {"WEB-INF", "js"};
			List<File> folders = new LinkedList<>();
			Files.walkFileTree(basePath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					if (isDir2Skip(basePath.relativize(dir))) {
						return FileVisitResult.SKIP_SUBTREE;
					} else {
						folders.add(dir.toFile());
						return FileVisitResult.CONTINUE;
					}
				}

				private boolean isDir2Skip(Path path) {
					for (String dir : dir2Skip) {
						if (path.toString().endsWith(dir)) {
							return true;
						}
					}
					return false;
				}
			});
			Collections.sort(folders);
			return folders;
		}
	}
}
