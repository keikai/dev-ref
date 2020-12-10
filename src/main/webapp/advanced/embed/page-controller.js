/**
* This js demonstrate:
* - Embed a zul with ZK embed API
* - communicate with ZK server composer by firing an event
*/
zEmbedded.load('embeddedZK', 'http://localhost:8080/dev-ref/advanced/embed/embed.zul')
    .then(function(result) {

        //jq is a custom jQuery $ in zk
        jq('#a_load').on('click', () => {
            getSpreadsheet().fire("onImport", {file: 'demo_sample.xlsx'}, {toServer:true});
        });
        jq('#a_toggleToolbar').on('click', () => {
            getSpreadsheet().fire("onToggleVisibility", {ui: "toolbar"}, {toServer:true});
        });
        jq('#a_toggleSheetbar').on('click', () => {
            getSpreadsheet().fire("onToggleVisibility", {ui: "sheetbar"}, {toServer:true});
        });
        jq('#a_toggleFormulabar').on('click', () => {
            getSpreadsheet().fire("onToggleVisibility", {ui: "formulabar"}, {toServer:true});
        });
    })
    .catch(function(reason) {
        console.error("error", this, arguments);
    });

/** need to get spreadsheet widget by a selector each time
 * because spreadsheet widget invalidates itself in some cases e.g. importing a book
 **/
function getSpreadsheet(){
    return zk.Widget.$('$spreadsheet');
}
