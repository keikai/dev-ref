/**
 * a patch for https://tracker.zkoss.org/browse/KEIKAI-372
 * based on keikai 5.4.0
 * Purpose: scroll the focused cell to the first row in the visible area
 */
zk.afterLoad('zss', function() {
    var oldWidget = {};
    zk.override(zss.SSheetCtrl.prototype, oldWidget, {
    	_cmdRetrieveFocus: function (result) {
            var type = result.type,
                row = result.row,
                column = result.column;
            if (type == "moveto") {
                var visibleRange  = zss.SSheetCtrl._getVisibleRange(this);
                var nVisibleRow = visibleRange.bottom - visibleRange.top + 1;
                this.sp.scrollToVisible(row + nVisibleRow,column,  null, zss.SCROLL_DIR.VERTICAL, zss.SCROLL_POS.TOP);
                var that = this;
                setTimeout(function(){
                    that.activeBlock.loadForVisible();
                    that.dp.moveFocus(row, column, true, true, true);
                }, 50);
            } else if (type == "retrive") {
                this.dp._gainFocus(true, true);
            }
        },
    });
});
