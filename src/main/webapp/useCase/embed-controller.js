
zEmbedded.load('embed', 'http://localhost:8080/dev-ref/useCase/stock-search.zul')
    .then(function(result) {
        // add event listener for buttons
        jq('#search').on('click', () => {
            Controller.fireEvent('onSearch', Controller.getFilterCriteria());
        });
        jq('#exprtExcel').on('click', () => {
            Controller.fireEvent('onExportExcel', null);
        });
        jq('#exprtPdf').on('click', () => {
            Controller.fireEvent('onExportPdf', null);
        });
    })
    .catch(function(reason) {
        console.error("ZK mounting error: ", this, arguments);
    });

class Controller{
    /** implement application logic at server
     */
    static fireEvent(eventName, parameter){
         Controller.getSpreadsheet().fire(eventName, parameter, {toServer:true});
    }

    static getSpreadsheet(){
        return zk.Widget.$('$spreadsheet');
    };

    static getFilterCriteria(){
        return {category: jq('#category').val(),
                min: jq('#min').val(),
                max: jq('#max').val()};
    }

    /** render vendor info on the page
    */
    static showVendor(vendor){
        jq('#name').text(vendor.name);
        jq('#tel').text(vendor.tel);
        jq('#email').text(vendor.email);
    }
}





