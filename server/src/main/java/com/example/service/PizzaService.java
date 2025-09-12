package com.example.service;

import com.example.dao.IngredientDao;
import com.example.dao.PizzaDao;
import com.example.dao.PizzaIngredientDao;
import com.example.dao.PizzaVariantDao;
import com.example.dao.impl.IngredientDaoImpl;
import com.example.dao.impl.PizzaDaoImpl;
import com.example.dao.impl.PizzaIngredientDaoImpl;
import com.example.dao.impl.PizzaVariantDaoImpl;
import com.example.dto.PizzaDto;
import com.example.exception.NotFoundException;
import com.example.model.Ingredient;
import com.example.utils.mapper.PizzaMapper;
import com.example.model.Pizza;
import com.example.model.PizzaVariant;
import com.example.dto.PizzaIngredientView;
import com.example.dto.IngredientTypeView;

import java.util.List;
import java.util.stream.Collectors;

public class PizzaService {

    private final PizzaDao pizzaDao = new PizzaDaoImpl();
    private final PizzaVariantDao variantDao = new PizzaVariantDaoImpl();



    public PizzaDto get(int id, boolean withVariants) throws NotFoundException {
        Pizza p = pizzaDao.findById(id);
        if (p == null) throw new NotFoundException("pizza_not_found");
        if (withVariants) p.setVariants(variantDao.findByPizzaId(id));
        return PizzaMapper.toDto(p);
    }


    public List<PizzaDto> list(boolean onlyAvailable, boolean withVariants) {
        List<Pizza> pizzas = onlyAvailable ? pizzaDao.findAvailable() : pizzaDao.findAll();
        if (withVariants) {
            for (Pizza p : pizzas) {
                p.setVariants(variantDao.findByPizzaId(p.getId()));
            }
        }
        return pizzas.stream().map(PizzaMapper::toDto).collect(Collectors.toList());
    }


    public PizzaDto create(PizzaDto dto) {
        if (dto == null) throw new IllegalArgumentException("PizzaDto is null");
        if (dto.id() != null) throw new IllegalArgumentException("New pizza must not have id");

        Pizza toSave = PizzaMapper.fromDto(dto);
        Pizza saved = pizzaDao.save(toSave);
        if (saved == null) throw new RuntimeException("pizza_create_failed");

        if (dto.variants() != null && !dto.variants().isEmpty()) {
            List<PizzaVariant> variants = PizzaMapper.variantsFromDto(dto.variants(), saved.getId());
            for (PizzaVariant v : variants) {
                if (variantDao.save(v) == null) {
                    pizzaDao.delete(saved.getId());
                    throw new RuntimeException("pizza_variant_create_failed");
                }
            }
        }

        saved.setVariants(variantDao.findByPizzaId(saved.getId()));
        return PizzaMapper.toDto(saved);
    }


    public PizzaDto update(PizzaDto dto) {
        if (dto == null || dto.id() == null) throw new IllegalArgumentException("PizzaDto.id is required");

        Pizza existing = pizzaDao.findById(dto.id());
        if (existing == null) throw new NotFoundException("pizza_not_found");

        Pizza toUpdate = PizzaMapper.fromDto(dto);
        Pizza updated = pizzaDao.update(toUpdate);
        if (updated == null) throw new RuntimeException("pizza_update_failed");

        if (dto.variants() != null) {
            variantDao.deleteAllByPizzaId(dto.id());
            List<PizzaVariant> variants = PizzaMapper.variantsFromDto(dto.variants(), dto.id());
            for (PizzaVariant v : variants) {
                if (variantDao.save(v) == null) {
                    updated.setVariants(variantDao.findByPizzaId(dto.id()));
                    return PizzaMapper.toDto(updated);
                }
            }
        }

        updated.setVariants(variantDao.findByPizzaId(dto.id()));
        return PizzaMapper.toDto(updated);
    }

    public PizzaDto delete(int id) {
        Pizza deleted = pizzaDao.delete(id);
        if (deleted == null) throw new NotFoundException("pizza_not_found");
        return PizzaMapper.toDto(deleted);
    }

}
