/**
* a customization to cell clicking: differentiate clicking the left and right part of a cell.
*/
zk.afterLoad('zss', function() {
    var exWidget = {};
	zk.override(zss.Spreadsheet.prototype, exWidget, {
        /**
         * Fires Cell Event
         * <p>
         * @param string type
         */
        fireCellEvt: function (type, shx, shy, mousemeta, row, col, pageX, pageY, field) {
            if ('af'==type && this.isProtect() &&
                !this.sheetCtrl._wgt.allowAutoFilter) { //forbid using filter under protection
                return;
            }

            var sheetId = this.getSheetId(),
                prop = {type: type, shx: shx, shy: shy, key: mousemeta, sheetId: sheetId,
                        row: row, col: col,
                        mx: pageX, my: pageY,
                    };
            if (field)
                prop.field = field;
            if (this._isFireCellEvt(type)) {
                //1995689 selection rectangle error when listen onCellClick,
                //use timeout to delay mouse click after mouse up(selection)
                var self = this;

                var section = determineClickedSection(this, pageX, row, col);

                setTimeout(function() {
                    self.fire('onZSSCellMouse',	prop, {toServer: true}, 25);
                    self.fire('onCellClickSection',	{section: section}, {toServer: true}, 25);
                }, 0);
            }
            var evtName = zss.Spreadsheet.CELL_MOUSE_EVENT_NAME[type];
            if (evtName) {
                var e = new zk.Event(this, evtName, prop);
                e.auStopped = true;
                this.fireX(e);
            }
        },
	});

	/**
	* a cell is divided into 2 equal size sections: left and right.
	*/
	function determineClickedSection(spreadsheet, pageX, row, col){
        var cellLeftBorderX = spreadsheet.sheetCtrl.getCell(row, col).$n().getBoundingClientRect().left;
        var cellWidth = spreadsheet.sheetCtrl.getCell(row, col).$n().getBoundingClientRect().width; //include borders
        if (pageX > cellLeftBorderX + cellWidth/2){
            return 'right';
        }else{
            return 'left';
        }
	}
});//zk.afterLoad