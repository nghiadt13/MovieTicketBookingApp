package com.apibackend.AppBackend.review.exception

class ReviewNotFoundException(message: String) : RuntimeException(message)

class ReviewAlreadyExistsException(message: String) : RuntimeException(message)

class UnauthorizedReviewAccessException(message: String) : RuntimeException(message)

class InvalidBookingException(message: String) : RuntimeException(message)

class ShowtimeNotCompletedException(message: String) : RuntimeException(message)
