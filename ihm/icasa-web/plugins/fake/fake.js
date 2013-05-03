//my/shirt.js now does setup work
//before returning its module definition.
define("fake", function () {
    //Do setup work here
    console.log("fake module loaded !!!");

    return {
        moduleId: "fake"
    }
});
