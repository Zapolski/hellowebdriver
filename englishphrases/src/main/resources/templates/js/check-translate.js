
function checkTranslate(expected, actual) {

    expectedList = expected.split(" ");
    actualList = actual.split(" ");
    resultStr = '';

    var expIndex = 0;
    var actIndex = 0;
    var expStr = '';

    while (!actualList.length == 0 && !expectedList.length == 0) {

        if (expIndex < expectedList.length) {
            expStr = expectedList[expIndex];
        } else {
            expStr = "";
        }
        var actStr = actualList[actIndex];

        if (actStr == expStr) {
            resultStr = resultStr + "<span class='right'>" + actStr + " " + "</span>";//правильно написано, на своем месте
            expectedList.splice(expIndex, 1);
        } else {
            if (!(expectedList.indexOf(actStr) == -1)) {
                resultStr = resultStr + "<span class='good'>" + actStr + " " + "</span>";//присутствует в предложении (правильно написано), но не на своем месте
                expectedList.splice(actStr, 1);
            } else {
                resultStr = resultStr + "<span class='wrong'>" + actStr + " " + "</span>";//не присутствет в предложении (неправильно написно)
                expIndex++;
            }
        }
        actualList.splice(actIndex, 1);
    }

    actualList.forEach(function (entry) {
        resultStr = resultStr + "<span class='wrong'>" + entry + " " + "</span>";
    });

    return resultStr;
}