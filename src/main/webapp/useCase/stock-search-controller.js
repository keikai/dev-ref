
zEmbedded.load('embed', 'http://localhost:8080/dev-ref/useCase/stock-search.zul')
    .then(function(result) {
        // add event listener for buttons
        $('#search').on('click', () => {
            Controller.fireEvent('onSearch', Controller.getFilterCriteria());
        });

        $('#exprtExcel').on('click', () => {
            Controller.fireEvent('onExportExcel', null);
        });

        $('#exprtPdf').on('click', () => {
            Controller.fireEvent('onExportPdf', null);
        });
    })
    .catch(function(reason) {
        console.error("ZK mounting error: ", this, arguments);
    });

class Controller{
    /** fire an event to invoke an event listener at the server-side */
    static fireEvent(eventName, parameter){
         Controller.getSpreadsheet().fire(eventName, parameter, {toServer:true});
    }

    /** get keikai spreadsheet js widget by ID selector "$id" */
    static getSpreadsheet(){
        return zk.Widget.$('$spreadsheet');
    };

    static getFilterCriteria(){
        return {"category": $('#category').val(),
                "min": $('#min').val(),
                "max": $('#max').val()};
    }

    /** render vendor info on the page
    */
    static showVendor(vendor){
        $('#name').text(vendor.name);
        $('#tel').text(vendor.tel);
        $('#email').text(vendor.email);
    }
}





