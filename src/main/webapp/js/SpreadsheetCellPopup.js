/**
* a custom popup that opens aside a cell.
*/
zk.afterLoad('zul.wgt', function() {
    SpreadsheetCellPopup = zk.$extends(zul.wgt.Popup,{
        openAtCell : function(ref, offset, position, opts, row, col)  {
            var wgt = zk.$('#'+ref).sheetCtrl.getCell(row,col).$n();
            this.open(wgt,offset, position, opts);
        }
    });
});//zk.afterLoad
