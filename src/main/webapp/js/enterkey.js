/**
 * Purpose: when pressing the enter key, the selection box moves right
 * Based on version: 
 */
zk.afterLoad('zss', function() {
    var MOVE_DOWN = "movedown";
    var MOVE_RIGHT = "moveright";
    var MOVE_LEFT = "moveleft";
    var MOVE_UP = "moveup";
    var MOVE_DIRECTION = MOVE_RIGHT; //after pressing enter key, the direction a selection moves
	function isAsciiCharkey (keycode) {
		//48-57 number, 65-90 alpha,
		//number pad
		//0-9 96-105
		//* 106 + 107 - 109 . 110  / 111
		//special
		//;: 186  =+ 187 ,< 188  -_ 189   .> 190  /? 191  `~ 192
		//\| 220  } 221 '" 222 [{ 219
		var r = ((keycode >= 48 && keycode <= 57) ||
			(keycode >= 65 && keycode <= 90) || (keycode >= 96 && keycode <= 105)),
			i = _skey.length;
		if(r) return true;
		while (i--)
			if(keycode == _skey[i]) return true;
		if (zk.opera && _opearKey.$contains(keycode))
			return true;
		//firefox fire +(61) ;(59) -(173)
		if(zk.gecko && (keycode == 61 || keycode == 59 || keycode == 173)) return true;

		return false;
	}
    var exWidget = {};
    zk.override(zss.SSheetCtrl.prototype, exWidget, {
        _doKeydown: function(evt) {
            this._skipress = false;
            //wait async event, skip
            //handle spreadsheet common keydown event
            if (this.isAsync()) return;

            //ctrl-paste: avoid multi-paste same clipboard content to focus textarea
            if (this._wgt._ctrlPasteDown)
                evt.stop();
            var keycode = evt.keyCode,
                ctrl;
            switch (keycode) {
            case 33: //PgUp
                this.dp.movePageup(evt);
                evt.stop();
                break;
            case 34: //PgDn
                this.dp.movePagedown(evt);
                evt.stop();
                break;
            case 35: //End
                if (this.isAllowKeyNavigation()) {
                    this.dp.moveEnd(evt);
                    //evt.stop(); //ZSS-181
                }
                break;
            case 36: //Home
                if (this.isAllowKeyNavigation()) {
                    this.dp.moveHome(evt);
                    //evt.stop(); //ZSS-181
                }
                break;
            case 37: //Left
                if (this.isAllowKeyNavigation()) {
                    this.dp.moveLeft(evt);
                    //evt.stop(); //ZSS-181
                }
                break;
            case 38: //Up
                if (this.isAllowKeyNavigation()) {
                    this.dp.moveUp(evt);
                    //evt.stop(); //ZSS-181
                }
                break;
            case 9://tab;
                if (this.state == zss.SSheetCtrl.EDITING){
                    if (evt.altKey || evt.ctrlKey)
                        break;
                    this.dp.stopEditing(evt.shiftKey ? "moveleft" : "moveright");//invoke move right after stopEdit
                    evt.stop();
                    break;
                }
                if (evt.shiftKey) {
                    this.dp.moveLeft();
                    evt.stop();
                } else if (!(evt.altKey || evt.ctrlKey)) {
                    this.dp.moveRight();
                    evt.stop();
                }
                break;
            case 39: //Right
                var info = this.editingFormulaInfo;
                if (this.isAllowKeyNavigation()) {
                    this.dp.moveRight(evt);
                    //evt.stop(); //ZSS-181
                }
                break;
            case 40: //Down
                if (this.isAllowKeyNavigation()) {
                    this.dp.moveDown(evt);
                    //evt.stop(); //ZSS-181
                }
                break;
            case 229: //ZSS-378 Chinese Input keyCode is always 229 in chrome/IE(8-10) (no spec.)
                // ZSS-737: other browsers listen composition event to catch IME input.
                if (zk.ie && zk.ie < 10 && this.state == zss.SSheetCtrl.FOCUSED) { //enter editing mode only when focused
                    //ZSS-1165
                    this._enterIMEEditing(evt);
                }
                break;
            case 113: //F2
                if(this.state == zss.SSheetCtrl.FOCUSED)
                    this._enterEditing(evt);
                //ZSS-1274 Support F2 to toggle arrow keys's function when editing formula
                else if (this.state == zss.SSheetCtrl.EDITING)
                    this.enableKeyNavigation = !this.enableKeyNavigation;
                evt.stop();
                break;
            case 13://Enter
                if (this.state == zss.SSheetCtrl.EDITING){
                    if (zk.mobile) {
                        return;
                    }
                    if(evt.altKey || evt.ctrlKey){
                        this.dp.getEditor().newLine();
                        evt.stop();
                        break;
                    }
                    this.dp.stopEditing(MOVE_DIRECTION);//invoke move down after stopEdit
                    evt.stop();
                } else if (this.state == zss.SSheetCtrl.FOCUSED) {
                    if (!this._wgt._copysrc) {
                        switch (MOVE_DIRECTION){
                            case MOVE_UP:
                                this.dp.moveDown(evt);
                                break;
                            case MOVE_RIGHT:
                                this.dp.moveRight(evt);
                                break;
                            case MOVE_DOWN:
                                this.dp.moveDown(evt);
                                break;
                            case MOVE_LEFT:
                                this.dp.moveLeft(evt);
                                break;
                        }
                        evt.stop();
                    }
                }
                break;
            case 27://ESC
                if (this.state == zss.SSheetCtrl.EDITING) {
                    this.dp.cancelEditing();
                    evt.stop();
                } else if(this.state == zss.SSheetCtrl.FOCUSED) {
                    //TODO should i send onCancel here?
                }
                break;
            }
            //in my notebook,some keycode ex : LEFT(37) and RIGHT(39) will fire keypress after keydown,
            //it confuse with the ascii value "%' and ''', so add this to do some controll in key press
            if (!isAsciiCharkey(keycode)) {
                this._skipress = true;
            }
        },
    });
	
});


