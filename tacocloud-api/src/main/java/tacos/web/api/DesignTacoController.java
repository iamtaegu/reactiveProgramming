//tag::recents[]
package tacos.web.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityLinks;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//end::recents[]
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
//tag::recents[]
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import tacos.Taco;
import tacos.data.TacoRepository;

@RestController
@RequestMapping(path="/design",                      // <1>
                produces="application/json")
@CrossOrigin(origins="*")        // <2>
public class DesignTacoController {

  private TacoRepository tacoRepo;

  @Autowired
  EntityLinks entityLinks;

  public DesignTacoController(TacoRepository tacoRepo) {
    this.tacoRepo = tacoRepo;
  }


  //start::다중반환
  @GetMapping("/recent")
  public Iterable<Taco> recentTacos() {                 //<3>
    PageRequest page = PageRequest.of(
            0, 12, Sort.by("createdAt").descending());
    return tacoRepo.findAll(page).getContent();
  }

  @GetMapping("/flux/recent")
  public Flux<Taco> flux_recentTacos() {
    return Flux.fromIterable(tacoRepo.findAll()).take(12);
    //tacoRepo 반환 타입이 Flux인 경우
    //return tacoRepo.findAll().take(12);
  }
  //end::다중반환

  //start::단일반환
  @GetMapping("/{id}")
  public Taco tacoById(@PathVariable("id") Long id) {
    Optional<Taco> optTaco = tacoRepo.findById(id);
    if (optTaco.isPresent()) {
      return optTaco.get();
    }
    return null;
  }

  @GetMapping("/flux/{id}")
  public Mono<Taco> flux_tacoById(@PathVariable("id") Long id) {
    return Mono.justOrEmpty(tacoRepo.findById(id));
  }
  //end::단일반환


  //tag::postTaco[]
  @PostMapping(consumes = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  //1. @RequestBody 처리 Taco entity 생성 (블로킹)
  //public Mono<Taco> postTaco(@RequestBody Mono<Taco> tacoMono) {
  public Taco postTaco(@RequestBody Taco taco) {
    //2. save 처리 (블로킹)
    return tacoRepo.save(taco);
    //return tacoRepo.saveAll(tacoMono).next();
  }
  //end::postTaco[]

}
