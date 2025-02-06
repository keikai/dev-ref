package io.keikai.devref.model;

import io.keikai.api.*;
import io.keikai.api.model.Book;
import io.keikai.devref.util.BookUtil;
import io.keikai.ui.Spreadsheet;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.Filedownload;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;

/**
 * This class shows exporter API
 * @author dennis
 *
 */
public class ExportComposer extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;
	
	@Wire
	private Spreadsheet ss;
	private Exporter exporter = Exporters.getExporter();
	private static final ScheduledExecutorService cleanupExecutor =
			Executors.newScheduledThreadPool(1);

	@Listen("onClick = #exportExcel; onExport=#ss")
	public void doExport() throws IOException{
		//2 ways
		exportInTempFile();
//		doExportInMemory();
	}

	/**
	 * Works well for larger files
	 * @throws IOException
	 */
	private void exportInTempFile() throws IOException {
		Exporter exporter = Exporters.getExporter();
		Book book = ss.getBook();
		Path tempFile = null;
		boolean exportSucceeded = false;

		try {
			tempFile = Files.createTempFile(Long.toString(System.currentTimeMillis()), "temp");
			try (OutputStream os = Files.newOutputStream(tempFile)) {
				exporter.export(book, os);
			}

			String dlname = BookUtil.suggestName(book);
			Filedownload.save(new AMedia(dlname, "xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
					tempFile.toFile(), true));
			exportSucceeded = true;

		} catch (IOException e) {
			// Clean up immediately if export fails
			if (tempFile != null) {
				Files.deleteIfExists(tempFile);
			}
			throw e;
		} finally {
			/** cannot delete the temporary file immediately in this method. because after this method completion the response goes back to the browser, zk client will send another request to download the exported file, so the file should exist at that moment. Hence, we have to delete it after a long enough time.
			 */
			if (exportSucceeded && tempFile != null) {
				final Path fileToDelete = tempFile; // Final copy for lambda
				cleanupExecutor.schedule(() -> {
					try {
						Files.deleteIfExists(fileToDelete);
					} catch (IOException e) {
						// Log error if cleanup fails
						e.printStackTrace();
					}
				}, 10, TimeUnit.SECONDS);
			}
		}
	}


	/**
	 * - Best for smaller files as it keeps everything in memory
	 * - No cleanup needed
	 * - Could cause memory issues with very large files
	 * @throws IOException
	 */
	public void doExportInMemory() throws IOException {
		Book book = ss.getBook();

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			exporter.export(book, baos);
			String bookName = BookUtil.suggestName(book);
			Filedownload.save(new AMedia(bookName, "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
					new ByteArrayInputStream(baos.toByteArray())));
		}
	}

}



