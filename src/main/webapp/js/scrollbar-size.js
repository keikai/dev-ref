/**
 * based on keikai 5.6.0
 * Purpose: set scrollbar width so that keikai can set expected width on .zsscroll-fix
 */
zk.afterLoad('zss', function() {
    var oldWidget = {};
    zk.override(zss.SSheetCtrl.prototype, oldWidget, {
        bind_: function () {
            // render the scrollbar in the original SSheetCtrl.bind_()
            oldWidget.bind_.apply(this, arguments);
            // should set the same size as you specified for .simplebar-vertical
            zss.Spreadsheet.scrollWidth = 20;
        }
    });
});