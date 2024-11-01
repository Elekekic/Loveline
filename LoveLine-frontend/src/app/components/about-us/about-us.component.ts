import { Component, ElementRef, ViewChild } from '@angular/core';
import { gsap } from 'gsap';
import { CarouselItem } from 'src/app/interfaces/carousel-item';

@Component({
  selector: 'app-about-us',
  templateUrl: './about-us.component.html',
  styleUrls: ['./about-us.component.scss']
})
export class AboutUsComponent {
  @ViewChild('carousel', { static: false }) carousel!: ElementRef;
  currentSlide = 0;

  items: CarouselItem[] = [
    { image: '../../../assets/img/C-item1.jpg', title: 'Aquarium Dates', description: 'Discover marine life together' },
    { image: '../../../assets/img/C-item2.jpg', title: 'Dinner Out', description: 'Enjoy a delicious meals and quality time' },
    { image: '../../../assets/img/C-item3.jpg', title: 'Photo Booth', description: 'Capture fun moments together in a photo booth' },
    { image: '../../../assets/img/C-item4.jpg', title: 'Table Games', description: 'Enjoy a competition with board games' },
    { image: '../../../assets/img/C-item5.jpg', title: 'Cinema Night', description: 'Watch a movie of your choice go to a cinema!' },
    { image: '../../../assets/img/C-item6.jpg', title: 'Go Kart Date', description: 'Race each other for an exciting and competitive experience' },
    { image: '../../../assets/img/C-item7.jpg', title: 'Skiing Holiday', description: 'Enjoy thrilling winter sports together' },
];




  
  ngOnInit(): void {
    this.iconsMenuAnimations();
  }

  ngAfterViewInit() {
    this.FaqAnimations();
    this.updateSlide();
  }

  previousSlide() {
    this.currentSlide = (this.currentSlide > 0) ? this.currentSlide - 1 : this.items.length - 1;
    this.updateSlide();
  }

  nextSlide() {
    this.currentSlide = (this.currentSlide < this.items.length - 1) ? this.currentSlide + 1 : 0;
    this.updateSlide();
  }


  updateSlide() {
    const offset = -this.currentSlide * 170;

    gsap.to(this.carousel.nativeElement, {
      x: offset,
      duration: 0.8,
      ease: 'power4.out'
    });
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
