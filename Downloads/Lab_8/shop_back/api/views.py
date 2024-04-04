from django.http.response import JsonResponse
from .models import *


# Create your views here.
def products(request):
    product = Product.objects.all()
    product_json = [p.to_json() for p in product]
    return JsonResponse(product_json, safe=False)


def product(request, product_id):
    try:
        p = Product.objects.get(id=product_id)
    except Product.DoesNotExist as error:
        return JsonResponse({'message: {error}'})
    return JsonResponse(p.to_json())

def all_categories(request):
    category = Category.objects.all()
    category_json = [c.to_json() for c in category]
    return JsonResponse(category_json, safe=False)

def category(request, category_id):
    try:
        c = Category.objects.get(id=category_id)
    except Category.DoesNotExist as error:
        return JsonResponse({'message: {error}'})
    return JsonResponse(c.to_json())

def products_by_category(request, category_id):
    products = Product.objects.all()
    products_json = [p.to_json() for p in products if p.product_id == category_id]
    return JsonResponse(products_json, safe=False)
