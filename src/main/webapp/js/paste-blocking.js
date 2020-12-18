/**
 * Based on keikai 5.5.0
 * Purpose: if the number of rows to paste exceeds the preloadRowSize, block pasting (do nothing), send an event to the server-side. otherwise, proceed to paste.
 * 1. If the preloadRowSize is unspecified, this script takes no effect.
 * 2. If the preloadRowSize is specified, when a user pastes a block of cells from Excel and the number of rows exceeds the preloadRowSize, it fires "onPasteOverLimit" event to the server.
 * 3. Application developer can handle the event to print a log or show a wanring message.
 */
zk.afterLoad('zss', function() {
    var exDataPanel = {};
    zk.override(zss.DataPanel.prototype, exDataPanel, {
        _speedCopy: function () {
            let inputNode = this.getInputNode();
            let vals = [];
            let sheet = this.sheet;
            let pos = sheet.getLastFocus();
            let left = pos.column;
            let top = pos.row;
            let ci = left;
            let right = left;
            let bottom = top;
            let clenmax = 0;
            // paste from Excel
            let inputFirstChild = inputNode.firstElementChild;
            let inputFirstChildTagName = inputFirstChild != undefined ? inputFirstChild.tagName : undefined;
            if ((inputFirstChildTagName === 'TABLE' || (inputFirstChildTagName === 'STYLE' && inputFirstChild.nextElementSibling && inputFirstChild.nextElementSibling.tagName === 'TABLE'))) {
                let tr = inputNode.getElementsByTagName('TR')[0];
                const trs = tr.parentElement.children;
                for (var i = 0; i < trs.length; i++) {
                    tr = trs[i];
                    let col = [];
                    if (tr.tagName == 'TR') {
                        var tds = tr.children;
                        for (var j = 0; j < tds.length; j++) {
                            var td = tds[j];
                            if (td.tagName == 'TD') {
                                col.push(td.innerText);
                            }
                        }
                    }
                    if (clenmax < col.length) {
                        clenmax = col.length;
                    }
                    vals.push(col);
                }
                right = left + clenmax - 1;
            } else { // paste from OS clipboard
                const rows = inputNode.innerText.split('\n');
                const rlen = rows.length;
                for (let r = 0; r < rlen; ++r) {
                    const row = rows[r];
                    const cols = row.split('\t');
                    const clen = cols.length;
                    const rmax = left + clen - 1;
                   if (clenmax < clen) {
                        clenmax = clen;
                    }
                    if (right < rmax) {
                        right = rmax;
                    }
                    vals.push(cols);
                }
            }

            if (this._wgt._preloadRowSize > 0 //-1 is unset
                && vals.length > this._wgt._preloadRowSize/2){
                // no client cache, notify the server side to handle
                this._wgt.fire('onPasteOverLimit',{token: "", sheetId: this.sheet.serverSheetId}, {toServer: true}, 25);
                return null;
            }
            return exDataPanel._speedCopy.apply(this, arguments);
        }
    });
});