/**
* disable sheet tab context menu.
*/
zk.afterLoad('zss', function() {
    var exWidget = {};
	zk.override(zss.SheetMenupopup.prototype, exWidget, {
        open: function (ref, offset, position, opts) {
            //no operation on purpose
        },
	});
});//zk.afterLoad