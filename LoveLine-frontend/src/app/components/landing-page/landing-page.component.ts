import { Component, ElementRef, Renderer2 } from '@angular/core';
import { gsap } from 'gsap';
import { Observer } from 'gsap/Observer';
declare const SplitType: any; // since the splitype on GSAP is premium this is an alternative
@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.scss'],
})
export class LandingPageComponent {
 
  constructor(private renderer: Renderer2, private el: ElementRef) {}

  ngOnInit(): void {

    this.landingPage();
    this.iconsMenuAnimations();
  }

  landingPage() {
    gsap.registerPlugin(Observer);

    let sections = document.querySelectorAll('section'),
      images = document.querySelectorAll('.bg'),
      outerWrappers = gsap.utils.toArray('.outer'),
      innerWrappers = gsap.utils.toArray('.inner'),
      currentIndex = -1,
      wrap = gsap.utils.wrap(0, sections.length),
      animating: boolean;

    let typeSplits = Array.from(sections).map((section) =>
      new SplitType(section, {
        types: 'lines, words, chars',
        tagName: 'span',
        preserveWhitespace: true,
      })
    );

    // I needed to make this quick set timeout for the links, because it was not keeping the spaces
  setTimeout(() => {
    const wordSpans = this.el.nativeElement.querySelectorAll('span.word');
    console.log('Word spans after SplitType:', wordSpans);

    wordSpans.forEach((span: HTMLElement) => {
      console.log('Applying margin to:', span);
      this.renderer.setStyle(span, 'margin-right', '0.2em');
    });
  }, 100); // Small delay

    gsap.set(outerWrappers, { yPercent: 100 });
    gsap.set(innerWrappers, { yPercent: -100 });

    function gotoSection(index: number, direction: number) {
      index = wrap(index); // this is to make sure it's valid
      animating = true;
      let fromTop = direction === -1,
        dFactor = fromTop ? -1 : 1,
        tl = gsap.timeline({
          defaults: { duration: 1.25, ease: 'power1.inOut' },
          onComplete: () => {
            animating = false;
          },
        });
      if (currentIndex >= 0) {
        gsap.set(sections[currentIndex], { zIndex: 0 });
        tl.to(images[currentIndex], { yPercent: -15 * dFactor }).set(
          sections[currentIndex],
          { autoAlpha: 0 }
        );
      }
      gsap.set(sections[index], { autoAlpha: 1, zIndex: 1 });
      tl.fromTo(
        [outerWrappers[index], innerWrappers[index]],
        {
          yPercent: (i) => (i ? -100 * dFactor : 100 * dFactor),
        },
        {
          yPercent: 0,
        },
        0
      )
        .fromTo(images[index], { yPercent: 15 * dFactor }, { yPercent: 0 }, 0)
        .fromTo(
          typeSplits[index].words,
          {
            autoAlpha: 0,
            yPercent: 150 * dFactor,
          },
          {
            autoAlpha: 1,
            yPercent: 0,
            duration: 1,
            ease: 'power2',
            stagger: {
              each: 0.02,
              from: 'random',
            },
          },
          0.2
        );
      currentIndex = index;
    }

    Observer.create({
      type: 'wheel,touch,pointer',
      wheelSpeed: -1,
      onDown: () => !animating && gotoSection(currentIndex - 1, -1),
      onUp: () => !animating && gotoSection(currentIndex + 1, 1),
      tolerance: 10,
      preventDefault: false,
    });

    gotoSection(0, 1);
  }

  iconsMenuAnimations(): void {
    document.querySelectorAll('a').forEach(function (icon) {
      const svg = icon.querySelector('svg');

      icon.addEventListener('mouseenter', function () {
        gsap.to(svg, {
          duration: 0.1,
          opacity: 1,
          scale: 1.1,
          ease: 'power4.out',
        });
      });

      icon.addEventListener('mouseleave', function () {
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
