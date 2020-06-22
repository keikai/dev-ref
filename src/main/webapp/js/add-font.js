/**
 * purpose: add more font families in the toolbar
 * base version: 5.2.0
 */
zk.afterLoad('zss', function() {
    var exWidget = {};
	zk.override(zss.ButtonBuilder.prototype, exWidget, {
        fontFamily: function (data) {
            let fonts =  ['Calibri', 'Arial', 'Arial Black', 'Comic Sans MS',
                                            'Courier New', 'Georgia', 'Impact',
                                            'Lucida Console', 'Lucida Sans Unicode',
                                            'Palatino Linotype', 'Tahoma', 'Times New Roman',
                                            'Trebuchet MS', 'Verdana', 'MS Sans Serif',
                                            'MS Serif'];
            this.addExtraFonts(fonts);
            let wgt = this._wgt,
                btn = new zss.KToolbarCombobox(this, 'fontFamily', {getterFunction: 'getFontName'}),
                menupopup = new zss.KMenupopup(wgt, 'fontFamily',
                   fonts, 'kfont');
            let items = menupopup.items;
            for (let i = 0; i < items.length; i++) {
                items[i].fireToolbarAction = function (action, props) {
                    props['name'] = action;
                    wgt.fireToolbarAction('fontFamily', props);
                }
            }
            btn.setPopup(menupopup);
            return btn;
        },
        addExtraFonts: function(fonts){
            fonts.push('標楷體');
        }
    });
});//zk.afterLoad