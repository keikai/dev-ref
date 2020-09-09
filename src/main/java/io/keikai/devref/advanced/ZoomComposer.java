package io.keikai.devref.advanced;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Iframe;

public class ZoomComposer extends org.zkoss.zk.ui.select.SelectorComposer<Component> {

	@Wire
	private Iframe editorFrame;
	
	private Double currentZoom = 1D;
	
	private String getZoomStyle(Double zoomValue) {
		return     "width: "+zoomValue*100+"%; height: "+zoomValue*100+"%; transform: scale("+1/zoomValue+"); transform-origin: 0 0;";
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		editorFrame.setStyle(getZoomStyle(currentZoom));
		editorFrame.setSrc("./iframeeditor.zul?target=/WEB-INF/books/demo_sample.xlsx");
	}	
	
	@Listen("onClick=#zoomOut")
	public void zoomOut() {
		currentZoom += 0.1;
		currentZoom = Math.min(2, currentZoom);
		editorFrame.setStyle(getZoomStyle(currentZoom));
	}
	@Listen("onClick=#zoomReset")
	public void zoomReset() {
		currentZoom = 1D;
		editorFrame.setStyle(getZoomStyle(currentZoom));
	}
	@Listen("onClick=#zoomIn")
	public void zoomIn() {
		currentZoom -= 0.1;
		currentZoom = Math.max(0.4, currentZoom);
		editorFrame.setStyle(getZoomStyle(currentZoom));
	}
	
	@Listen("onClick=#doc1")
	public void loadDoc1(){
		editorFrame.setSrc("./iframeeditor.zul?target=/WEB-INF/books/demo_sample.xlsx");
	}
	@Listen("onClick=#doc2")
	public void loadDoc2(){
		editorFrame.setSrc("./iframeeditor.zul?target=/WEB-INF/books/tradeTemplate.xlsx");
	}
}
