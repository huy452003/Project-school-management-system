import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import './ScrollToTopButton.css'

const ScrollToTopButton = () => {
  const [isVisible, setIsVisible] = useState(false)

  useEffect(() => {
    const toggleVisibility = () => {
      if (window.pageYOffset > 300) {
        setIsVisible(true)
      } else {
        setIsVisible(false)
      }
    }

    window.addEventListener('scroll', toggleVisibility)

    return () => {
      window.removeEventListener('scroll', toggleVisibility)
    }
  }, [])

  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    })
  }

  return (
    <>
      {isVisible && (
        <div className="floating-buttons">
          <Link
            to="/contact"
            className="floating-btn messenger-btn"
            aria-label="Messenger"
            title="Chat với chúng tôi qua Messenger"
          >
            <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 0C5.373 0 0 5.373 0 12c0 3.584 1.948 6.728 4.851 8.421L3.5 24l3.649-1.351C8.5 23.5 10.2 24 12 24c6.627 0 12-5.373 12-12S18.627 0 12 0z"/>
              <path d="M13.5 6.5L10 10l3.5 3.5L12 15l-3.5-3.5L12 8l1.5-1.5z" fill="white"/>
            </svg>
          </Link>
          <Link
            to="/contact"
            className="floating-btn zalo-btn"
            aria-label="Zalo"
            title="Chat với chúng tôi qua Zalo"
          >
            <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8z"/>
              <path d="M9 8h6v1.5H9V8zm0 2.5h6v1.5H9v-1.5zm0 2.5h4.5v1.5H9V13z" fill="white"/>
              <path d="M12 7l-2 2h4l-2-2z" fill="white"/>
            </svg>
          </Link>
          <button
            className="scroll-to-top-btn"
            onClick={scrollToTop}
            aria-label="Scroll to top"
          >
            ↑
          </button>
        </div>
      )}
    </>
  )
}

export default ScrollToTopButton

