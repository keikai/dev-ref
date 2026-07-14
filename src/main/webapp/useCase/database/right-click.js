/**
 * The workaround is to make right-clicking always open the custom context menu.
 * The issue is: 
 *     when the custom context menu is open, right click another cell doesn't open the context menu again. 
 * It just disappears. 
 */
jq(document).mousedown(function(event){
	if (event.which == 3){ //right click
		zk.Widget.$('$ss').focus();
	}
});