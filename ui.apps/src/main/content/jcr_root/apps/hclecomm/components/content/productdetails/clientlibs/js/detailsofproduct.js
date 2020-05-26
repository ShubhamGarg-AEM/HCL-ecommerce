$(document).ready(function () {
	$('.quantity').val("1");
	$('.product-details-cmp__prduct-price-sign').css("display", "none");
	$('.product-details-cmp--no-product').css("display", "none");
	$('.cmp-button').addClass("btn-product-card-disabled");
	$(".cmp-button").attr('disabled', 'disabled');
	const firstSizeSelected = $(".product-details-cmp__product-size--item:first-child").text();
	$('.select-size').text(firstSizeSelected);
	$(".product-details-cmp__product-size--item:first-child").addClass("selected-size");
	const xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function () {
		if (this.readyState == 4 && this.status == 200) {
			const productDetail = JSON.parse(this.responseText)
			$('.product-details-cmp__prduct-price-sign').css("display", "inline-block");
			$(".product-details-cmp__prduct-price").text(productDetail[0].price);
			const isProductAvilable = (productDetail[0].stock === 'true');
			if (!isProductAvilable) {
				$('.cmp-button').addClass("btn-product-card-disabled");
				$(".cmp-button").attr('disabled', 'disabled');
				$('.product-details-cmp--no-product').css("display", "block");
			} else {
				$('.cmp-button').removeClass("btn-product-card-disabled");
				$(".cmp-button").removeattr('disabled');
				$('.product-details-cmp--no-product').css("display", "none");

			}

		}
	};
	xhttp.open("GET", "/bin/hclecomm/productDetails?sku=" + "24-WG087", true);
	xhttp.send();


});

function onSizeSelection() {
	$(".selected-size").removeClass("selected-size");
	//$(this).addClass("selected-size");


	$target = $(event.target);
	$target.addClass('selected-size');
	$(".select-size").text(event.target.textContent);
}

function onQuantityChnage(para) {
	let getQuant = Number($('.quantity').val());
	if (para === '-') {
		if (getQuant === 0) {
			return
		} else {
			getQuant = getQuant - 1;
		}
		$('.quantity').val(getQuant);
		return
	}


	if (getQuant === 999) {
		return
	} else {
		getQuant = getQuant + 1;
	}
	$('.quantity').val(getQuant);
}


function onQuantityKeyUp(event) {
	const getCount = event.target.value;
	getCount < 0 ? $('.quantity').val(0) : null;
	getCount > 999 ? $('.quantity').val(999) : null;
}