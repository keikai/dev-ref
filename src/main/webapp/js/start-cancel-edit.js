/**
* an onClick listener to start editing a cell
*/
jq("#edit").on('click', function(){
    var focusCell = function (spreadsheet, rowIndex, columnIndex){
        var cellParam = {row: rowIndex,
                         column: columnIndex,
                         type: 'moveto'};
        spreadsheet.setRetrieveFocus(cellParam);
    }

    var spreadsheet = zk.Widget.$("$spreadsheet"); //spreadsheet id
//    var spreadsheet = zk.Widget.$("@spreadsheet"); //if there is only 1 spreadsheet widget, just get the widget with class

    focusCell(spreadsheet, 9, 9);
    spreadsheet.sheetCtrl._enterEditing({});
    //cancel after 3 seconds
    setTimeout(spreadsheet.sheetCtrl.dp.cancelEditing.bind(spreadsheet.sheetCtrl.dp), 3000);
});



