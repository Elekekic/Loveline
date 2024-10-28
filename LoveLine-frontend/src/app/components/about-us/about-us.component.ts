import { Component } from '@angular/core';
import { gsap } from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';

@Component({
  selector: 'app-about-us',
  templateUrl: './about-us.component.html',
  styleUrls: ['./about-us.component.scss']
})
export class AboutUsComponent {

  
  ngOnInit(): void {
    this.iconsMenuAnimations();
  }

  ngAfterViewInit() {
    this.FaqAnimations();
  }



  
  iconsMenuAnimations(): void {
    document.querySelectorAll('a').forEach(function (link) {
      const svg = link.querySelector('svg');

      link.addEventListener('mouseenter', function () {
        gsap.to(svg, {
          duration: 0.1,
          opacity: 1,
          scale: 1.1,
          ease: 'power4.out',
        });
      });

      link.addEventListener('mouseleave', function () {
        gsap.to(svg, {
          duration: 0.1,
          opacity: 0,
          scale: 0.5,
          ease: 'power4.in',
        });
      });
    });
  }
  FaqAnimations(): void {
    const faqToggles = document.querySelectorAll('.faq-toggle');
    const faqs = document.querySelectorAll('.faq-body');

    faqToggles.forEach((button) => {
      button.addEventListener('click', () => {
        const targetSelector = button.getAttribute('data-target');
        if (targetSelector) {
          const target = document.querySelector(targetSelector) as HTMLElement;
          if (target) {
            const isOpen = target.classList.contains('open');
            if (isOpen) {
              this.closeFAQ(target);
            } else {
              this.openFAQ(target);
            }
          }
        }
      });
    });

    faqs.forEach((faq) => {
      faq.addEventListener('click', () => {
        this.closeFAQ(faq as HTMLElement);
      });
    });
  }

  openFAQ(target: HTMLElement): void {
    gsap.to(target, {
      duration: 0.4,
      maxHeight: '12vh',
      opacity: 1,
      ease: 'power4.inOut',
      onComplete: () => {
        target.classList.add('open');
      },
    });
  }

  closeFAQ(target: HTMLElement): void {
    gsap.to(target, {
      duration: 0.8,
      maxHeight: 0,
      opacity: 0,
      ease: 'power4.inOut',
      onComplete: () => {
        target.classList.remove('open');
      },
    });
  }
}
