package com.example.stockify.recetaBase.infrastructure;

import com.example.stockify.recetaBase.domain.RecetaBase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecetaBaseRepository extends JpaRepository<RecetaBase, Long> {
}
