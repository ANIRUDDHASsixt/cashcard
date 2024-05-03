package example.cashcard.adapter.handler.rest.controller;

import example.cashcard.adapter.persistance.CashCardRepository;
import example.cashcard.domain.cashcard.CashCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/cashcards")
final class CashCardController {
    private final CashCardRepository cashCardRepository;

    private CashCardController(final CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(final @PathVariable Long requestedId, final Principal principal) {
        CashCard cashCard = findCashCard(requestedId, principal);
        if (cashCard != null) {
            return ResponseEntity.ok(cashCard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(final @RequestBody CashCard newCashCardRequest,
                                                final UriComponentsBuilder ucb,
                                                final Principal principal) {
        CashCard cashCardWithOwner = new CashCard(null, newCashCardRequest.amount(), principal.getName());
        CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(final Pageable pageable, final Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putCashCard(final @PathVariable Long requestedId,
                                             final @RequestBody CashCard cashCardUpdate,
                                             final Principal principal) {
        CashCard cashCard = findCashCard(requestedId, principal);
        if (cashCard != null) {
            CashCard updatedcashCard = new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());
            cashCardRepository.save(updatedcashCard);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private CashCard findCashCard(final Long requestedId, final Principal principal) {
        return cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(final @PathVariable Long id, final Principal principal) {
        if (cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
            cashCardRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
