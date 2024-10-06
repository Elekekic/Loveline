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
  
}
