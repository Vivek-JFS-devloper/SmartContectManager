//alert("js page is loaded");
console.log("js page si loaded")

const toggles = ()=>{
    if($('.sideBar').is(':visible')){
        //true part side bar to do close
        $('.sideBar').css('display','none')
        $('.content').css('margin-left','1%')

    }else{
        //false part sidevar to do show
        $('.sideBar').css('display','block')
        $('.content').css('margin-left','20%')
    }
};