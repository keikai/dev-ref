/**
* a patch for ZSS-1305
*/
zk.afterLoad('zss', function() {
    var exWidget = {};
	zk.override(zss.Spreadsheet.prototype, exWidget, {
        afterKeyDown_: function (evt) {
            var sheet = this.sheetCtrl;
            if (sheet && sheet.state != zss.SSheetCtrl.EDITING) {
                var data = evt.data,
                    sel = sheet.getLastSelection();
                if (sel) {
                    data.tRow = sel.top;
                    data.lCol = sel.left;
                    data.bRow = sel.bottom;
                    data.rCol = sel.right;
                    data.type = sheet.selType ? sheet.selType : zss.SEL.CELL; //ZSS-717
                }
                data.sheetId = this.getSheetId();
                this.$supers('afterKeyDown_', arguments);
                //feature #26: Support copy/paste value to local Excel
                var keyCode = evt.keyCode;
                if (this.isListen('onCtrlKey', {any:true})
                    && (keyCode == 67 || keyCode == 86)
                    && evt.ctrlKey) //fix zss-1305
                    { //67: ctrl-c; 86: ctrl-v
                    var parsed = this._parsedCtlKeys,
                        ctrlKey = evt.ctrlKey ? 1: evt.altKey ? 2: evt.shiftKey ? 3: 0;
                    if (parsed &&
                        parsed[ctrlKey][keyCode]) {
                        //Widget.js will stop event, if onCtrlKey reg ctrl + c and ctrl + v. restart the event
                        evt.domStopped = false;
                    }

                    // ZSS-737: prevent focus lost when focus to textarea.
                    sheet.isPasteFromClipboard = true;
                    sheet.dp.selectInputNode();
                    var that = this;
                    //do the copy on the sheet!
                    // 20140509, RaymondChao: focustag's value becames empty when paste twice or more times without setTimeout.
                    setTimeout(function(){ that.doPaste(); }, 0);
                }
            }
            //avoid onCtrlKey to be eat in editing mode.
        },
	});
});//zk.afterLoad