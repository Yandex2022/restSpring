package restapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restapp.exception.ItemNotFoundException;
import restapp.model.ShopUnit;
import restapp.model.ShopUnitImportRequest;
import restapp.model.ShopUnitStatisticResponse;
import restapp.service.ProductService;
import restapp.util.ISO_8601;

import java.util.Date;
import java.util.UUID;

@RestController
public class Controller {

    @Autowired
    private ProductService productService;


    @PostMapping("/imports")
    public ResponseEntity importRequest(@RequestBody ShopUnitImportRequest request){
        try{
            productService.importProducts(request);
            return ResponseEntity.ok("Данные обновлены");
        }
//        catch (ShopUnitImportException e){
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteRequest(@PathVariable("id") String str){
        try {
            UUID id = UUID.fromString(str);
            productService.deleteProduct(id);
            return ResponseEntity.ok("Удаление прошло успешно");
        }
        catch (ItemNotFoundException e){
            return ResponseEntity.notFound().build();
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity getRequest(@PathVariable("id") String str){
        try {
            UUID id = UUID.fromString(str);
            ShopUnit response = productService.getProductNodeInfo(id);
            return ResponseEntity.ok(response);
        }
        catch (ItemNotFoundException e){
            return ResponseEntity.notFound().build();
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/sales")
    public ResponseEntity getSales(@RequestParam("date") String str){
        try{
            Date date = ISO_8601.strToDate(str);
            ShopUnitStatisticResponse response = productService.getSales(date);
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/node/{id}/statistic")
    public ResponseEntity getStatistic(@PathVariable("id") String strId,
                                       @RequestParam("dateStart") String strDateStart,
                                       @RequestParam("dateEnd") String strDateFinish) {
        try {
            UUID id = UUID.fromString(strId);
            Date dateStart = ISO_8601.strToDate(strDateStart);
            Date dareFinish = ISO_8601.strToDate(strDateFinish);
            ShopUnitStatisticResponse response = productService.getStatistic(id, dateStart, dareFinish);
            return ResponseEntity.ok(response);
        }
        catch (ItemNotFoundException e){
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
